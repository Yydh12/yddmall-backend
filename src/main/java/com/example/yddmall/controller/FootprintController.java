package com.example.yddmall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.service.BrowseHistoryService;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.vo.FootprintItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footprint")
@Tag(name = "浏览足迹接口")
public class FootprintController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private HttpServletRequest request;

    @Operation(summary = "记录商品浏览")
    @PostMapping("/item/{itemId}")
    public ApiResponse<Boolean> record(@PathVariable("itemId") Long itemId,
                                       @RequestParam(value = "skuId", required = false) Long skuId) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean ok = browseHistoryService.record(userId, itemId, skuId);
        return ResponseUtils.success(ok);
    }

    @Operation(summary = "分页查询我的商品浏览足迹")
    @GetMapping("/item")
    public ApiResponse<Page<FootprintItemVO>> list(@RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") long pageSize) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        Page<FootprintItemVO> page = browseHistoryService.list(userId, pageNum, pageSize);
        return ResponseUtils.success(page);
    }

    @Operation(summary = "删除单个商品足迹")
    @DeleteMapping("/item/{itemId}")
    public ApiResponse<Boolean> removeOne(@PathVariable("itemId") Long itemId) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean ok = browseHistoryService.remove(userId, itemId);
        return ResponseUtils.success(ok);
    }

    @Operation(summary = "清空我的商品浏览足迹")
    @DeleteMapping("/item")
    public ApiResponse<Boolean> clearAll() {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean ok = browseHistoryService.clear(userId);
        return ResponseUtils.success(ok);
    }
}