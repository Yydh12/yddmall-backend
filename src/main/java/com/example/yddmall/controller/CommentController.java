package com.example.yddmall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.dto.CommentDTO;
import com.example.yddmall.entity.Comment;
import com.example.yddmall.service.CommentService;
import com.example.yddmall.service.UserService;
import com.example.yddmall.entity.User;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comment")
@Tag(name = "商品评论接口")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @Operation(summary = "分页查询商品评论")
    @GetMapping("/item/{itemId}")
    public ApiResponse<IPage<CommentVO>> pageByItemId(@PathVariable Long itemId,
                                                      @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        try {
            Page<CommentVO> page = new Page<>(current, size);
            IPage<CommentVO> data = commentService.pageItemComments(page, itemId);
            return ResponseUtils.success(data);
        } catch (Exception e) {
            log.error("查询商品评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "查询评论失败");
        }
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public ApiResponse<Comment> addComment(@RequestBody CommentDTO dto, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            if (dto.getItemId() == null || dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "参数不合法");
            }
            Comment c = commentService.addComment(userId, dto);
            return ResponseUtils.success("评论成功", c);
        } catch (Exception e) {
            log.error("发表评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "发表评论失败");
        }
    }

    @Operation(summary = "店铺最近评论（当前登录商家）")
    @GetMapping("/store/recent")
    public ApiResponse<IPage<CommentVO>> recentStoreComments(HttpServletRequest request,
                                                             @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            log.info("allStoreComments - userId: {}", userId);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            User user = userService.getById(userId);
            log.info("allStoreComments - user: {}, merchantId: {}", user, user != null ? user.getMerchantId() : null);
            System.out.println(user);
            if (user.getRoleId() != 1) {
                if (user == null || user.getMerchantId() == null) {
                return ResponseUtils.error(ResponseCode.FORBIDDEN, "您不是商家或未绑定店铺");
            }
            }
            Page<CommentVO> page = new Page<>(current, size);
            IPage<CommentVO> data = commentService.pageStoreRecent(page, user.getMerchantId());
            return ResponseUtils.success(data);
        } catch (Exception e) {
            log.error("查询店铺最新评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "查询失败");
        }
    }

    @Operation(summary = "店铺全部评论（含隐藏，当前登录商家）")
    @GetMapping("/store/all")
    public ApiResponse<IPage<CommentVO>> allStoreComments(HttpServletRequest request,
                                                          @RequestParam(value = "current", required = false, defaultValue = "1") long current,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            log.info("allStoreComments - userId: {}", userId);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            User user = userService.getById(userId);
            log.info("allStoreComments - user: {}, merchantId: {}, roleId: {}", user, user != null ? user.getMerchantId() : null, user != null ? user.getRoleId() : null);

            Long sellerIdToQuery = null;
            if (user != null && user.getRoleId() != null && user.getRoleId() == 1) { // 管理员
                sellerIdToQuery = null; // 管理员查看所有评论
            } else if (user != null && user.getMerchantId() != null) { // 商家
                sellerIdToQuery = user.getMerchantId();
            } else {
                return ResponseUtils.error(ResponseCode.FORBIDDEN, "您不是商家或未绑定店铺");
            }

            Page<CommentVO> page = new Page<>(current, size);
            IPage<CommentVO> data = commentService.pageStoreAll(page, sellerIdToQuery);
            return ResponseUtils.success(data);
        } catch (Exception e) {
            log.error("查询店铺全部评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "查询失败");
        }
    }

    @Operation(summary = "回复用户评论（商家）")
    @PostMapping("/{commentId}/reply")
    public ApiResponse<Boolean> replyComment(@PathVariable Long commentId,
                                             @RequestBody com.example.yddmall.dto.CommentReplyDTO dto,
                                             HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            if (dto == null || dto.getReplyContent() == null || dto.getReplyContent().trim().length() < 1) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "回复内容不能为空");
            }
            boolean ok = commentService.replyToComment(commentId, userId, dto.getReplyContent().trim());
            if (ok) {
                return ResponseUtils.success("回复成功", true);
            } else {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "回复失败或评论不存在");
            }
        } catch (Exception e) {
            log.error("回复评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "系统错误，回复失败");
        }
    }

    @Operation(summary = "审核通过评论（设置为正常显示）")
    @PostMapping("/{commentId}/approve")
    public ApiResponse<Boolean> approveComment(@PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            boolean ok = commentService.updateStatus(commentId, 1);
            return ok ? ResponseUtils.success("审核通过", true)
                      : ResponseUtils.error(ResponseCode.BAD_REQUEST, "操作失败或评论不存在");
        } catch (Exception e) {
            log.error("审核通过失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "系统错误，审核失败");
        }
    }

    @Operation(summary = "审核拒绝/隐藏评论（设置为隐藏）")
    @PostMapping("/{commentId}/reject")
    public ApiResponse<Boolean> rejectComment(@PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            boolean ok = commentService.updateStatus(commentId, 0);
            return ok ? ResponseUtils.success("已隐藏", true)
                      : ResponseUtils.error(ResponseCode.BAD_REQUEST, "操作失败或评论不存在");
        } catch (Exception e) {
            log.error("隐藏评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "系统错误，隐藏失败");
        }
    }

    @Operation(summary = "删除评论（物理删除）")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            boolean ok = commentService.deleteComment(commentId);
            return ok ? ResponseUtils.success("删除成功", true)
                      : ResponseUtils.error(ResponseCode.BAD_REQUEST, "删除失败或评论不存在");
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "系统错误，删除失败");
        }
    }
}