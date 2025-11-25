package com.example.yddmall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.entity.Merchant;
import com.example.yddmall.service.FavoriteMerchantService;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
@Tag(name = "收藏夹接口")
public class FavoriteController {

    @Autowired
    private FavoriteMerchantService favoriteMerchantService;

    @Autowired
    private HttpServletRequest request;

    @Operation(summary = "添加店铺收藏")
    @PostMapping("/merchant/{merchantId}")
    public ApiResponse<Boolean> addFavorite(@PathVariable("merchantId") Long merchantId) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean ok = favoriteMerchantService.addFavorite(userId, merchantId);
        return ResponseUtils.success(ok);
    }

    @Operation(summary = "取消店铺收藏")
    @DeleteMapping("/merchant/{merchantId}")
    public ApiResponse<Boolean> removeFavorite(@PathVariable("merchantId") Long merchantId) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean ok = favoriteMerchantService.removeFavorite(userId, merchantId);
        return ResponseUtils.success(ok);
    }

    @Operation(summary = "分页查询我的收藏店铺")
    @GetMapping("/merchant")
    public ApiResponse<Page<Merchant>> listFavorites(@RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") long pageSize) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        Page<Merchant> page = favoriteMerchantService.listFavorites(userId, pageNum, pageSize);
        return ResponseUtils.success(page);
    }

    @Operation(summary = "检查店铺是否已收藏")
    @GetMapping("/merchant/{merchantId}/exists")
    public ApiResponse<Boolean> exists(@PathVariable("merchantId") Long merchantId) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean exists = favoriteMerchantService.isFavorite(userId, merchantId);
        return ResponseUtils.success(exists);
    }
}