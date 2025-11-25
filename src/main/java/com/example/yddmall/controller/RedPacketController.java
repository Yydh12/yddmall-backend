package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.entity.RedPacket;
import com.example.yddmall.entity.UserRedPacket;
import com.example.yddmall.service.RedPacketService;
import com.example.yddmall.service.UserService;
import com.example.yddmall.service.MerchantService;
import com.example.yddmall.entity.User;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.vo.UserRedPacketVO;
import com.example.yddmall.entity.Merchant;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/redPacket")
public class RedPacketController {

    @Resource
    private RedPacketService redPacketService;

    @Resource
    private UserService userService;

    @Resource
    private MerchantService merchantService;

    @GetMapping("/available")
    public ApiResponse<List<RedPacket>> listAvailable(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "itemId", required = false) Long itemId
    ) {
        // 当前仅按商家筛选；itemId 预留不做处理
        return ResponseUtils.success(redPacketService.listAvailable(merchantId));
    }

    /**
     * 公共接口：按商家列出所有红包（不限制有效期/状态），用于商品详情页展示“店铺所属红包”。
     * 若需要仅展示有效红包，请使用 /available
     */
    @GetMapping("/byMerchant")
    public ApiResponse<List<RedPacket>> listByMerchantPublic(
            @RequestParam(value = "merchantId") Long merchantId
    ) {
        if (merchantId == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "merchantId缺失");
        }
        return ResponseUtils.success(redPacketService.listByMerchant(merchantId));
    }

    @PostMapping("/claim/{id}")
    public ApiResponse<Boolean> claim(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        boolean ok = redPacketService.claim(id, userId);
        if (ok) return ResponseUtils.success(true);
        return ResponseUtils.error(ResponseCode.BAD_REQUEST, "领取失败或不满足条件");
    }

    @GetMapping("/mine")
    public ApiResponse<List<UserRedPacketVO>> mine(
            HttpServletRequest request,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "orderNo", required = false) String orderNo
    ) {
        Long userId = SessionUserUtils.getUserId(request);
        List<UserRedPacket> list;
        if (status == null && (orderNo == null || orderNo.isEmpty())) {
            list = redPacketService.listUserRedPackets(userId);
        } else {
            list = redPacketService.listUserRedPackets(userId, status, orderNo);
        }
        java.util.List<UserRedPacketVO> voList = new java.util.ArrayList<>();
        for (UserRedPacket urp : list) {
            if (urp == null) continue;
            UserRedPacketVO vo = new UserRedPacketVO();
            vo.setId(urp.getId());
            vo.setRedPacketId(urp.getRedPacketId());
            vo.setUserId(urp.getUserId());
            vo.setStatus(urp.getStatus());
            vo.setClaimedAt(urp.getClaimedAt());
            vo.setUsedAt(urp.getUsedAt());
            vo.setOrderNo(urp.getOrderNo());
            if (urp.getRedPacketId() != null) {
                RedPacket rp = redPacketService.getById(urp.getRedPacketId());
                if (rp != null) {
                    vo.setTitle(rp.getTitle());
                    vo.setAmount(rp.getAmount());
                    vo.setStartTime(rp.getStartTime());
                    vo.setEndTime(rp.getEndTime());
                    vo.setMerchantId(rp.getMerchantId());
                    if (rp.getMerchantId() != null) {
                        try {
                            Merchant m = merchantService.getById(rp.getMerchantId());
                            if (m != null) {
                                vo.setMerchantName(m.getMerchantName());
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
            voList.add(vo);
        }
        return ResponseUtils.success(voList);
    }

    /** 商家或管理员发布红包 */
    @PostMapping("/create")
    public ApiResponse<RedPacket> create(@RequestBody RedPacket redPacket, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限发布红包");
        }
        Long roleId = user.getRoleId();
        if (roleId == 1L) {
            // 管理员：允许指定任意商户ID，必须传 merchantId
            if (redPacket.getMerchantId() == null) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "管理员发布红包必须指定merchantId");
            }
        } else if (roleId == 3L) {
            // 商家：限定到自己的merchantId
            if (user.getMerchantId() == null) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "商家信息缺失，请联系平台");
            }
            redPacket.setMerchantId(user.getMerchantId());
        } else {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员或商家可发布红包");
        }
        try {
            RedPacket created = redPacketService.create(redPacket);
            return ResponseUtils.success(created);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "发布失败: " + e.getMessage());
        }
    }

    /** 发布记录列表：管理员可查看全部或指定商家；商家查看自己的 */
    @GetMapping("/published")
    public ApiResponse<List<RedPacket>> published(
            HttpServletRequest request,
            @RequestParam(value = "merchantId", required = false) Long merchantId
    ) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "请先登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限");
        }
        Long roleId = user.getRoleId();
        if (roleId == 1L) {
            if (merchantId != null) {
                return ResponseUtils.success(redPacketService.listByMerchant(merchantId));
            }
            return ResponseUtils.success(redPacketService.listAll());
        } else if (roleId == 3L) {
            Long mid = user.getMerchantId();
            if (mid == null) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "商家信息缺失");
            }
            return ResponseUtils.success(redPacketService.listByMerchant(mid));
        }
        return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员或商家可查看发布记录");
    }

    /** 编辑红包：管理员或归属商家 */
    @PutMapping("/update")
    public ApiResponse<RedPacket> update(@RequestBody RedPacket redPacket, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "请先登录");
        }
        if (redPacket == null || redPacket.getId() == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "ID不能为空");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限");
        }
        RedPacket exist = redPacketService.getById(redPacket.getId());
        if (exist == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "红包不存在");
        }
        Long roleId = user.getRoleId();
        boolean permitted = roleId == 1L
                || (roleId == 3L && exist.getMerchantId() != null && exist.getMerchantId().equals(user.getMerchantId()));
        if (!permitted) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限编辑该红包");
        }
        // 防止越权修改商户ID或剩余数量
        redPacket.setMerchantId(null);
        redPacket.setRemainingCount(null);
        try {
            RedPacket updated = redPacketService.update(redPacket);
            return ResponseUtils.success(updated);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "更新失败: " + e.getMessage());
        }
    }

    /** 启用红包 */
    @PostMapping("/enable/{id}")
    public ApiResponse<Boolean> enable(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "请先登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限");
        }
        RedPacket exist = redPacketService.getById(id);
        if (exist == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "红包不存在");
        }
        Long roleId = user.getRoleId();
        boolean permitted = roleId == 1L
                || (roleId == 3L && exist.getMerchantId() != null && exist.getMerchantId().equals(user.getMerchantId()));
        if (!permitted) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限操作该红包");
        }
        boolean ok = redPacketService.setStatus(id, 1);
        return ok ? ResponseUtils.success(true) : ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "操作失败");
    }

    /** 停用红包 */
    @PostMapping("/disable/{id}")
    public ApiResponse<Boolean> disable(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "请先登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限");
        }
        RedPacket exist = redPacketService.getById(id);
        if (exist == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "红包不存在");
        }
        Long roleId = user.getRoleId();
        boolean permitted = roleId == 1L
                || (roleId == 3L && exist.getMerchantId() != null && exist.getMerchantId().equals(user.getMerchantId()));
        if (!permitted) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "无权限操作该红包");
        }
        boolean ok = redPacketService.setStatus(id, 0);
        return ok ? ResponseUtils.success(true) : ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "操作失败");
    }
}