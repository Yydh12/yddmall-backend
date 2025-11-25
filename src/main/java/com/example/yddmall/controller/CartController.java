package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.entity.Cart;
import com.example.yddmall.service.CartService;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Tag(name = "购物车接口")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    @Operation(summary = "添加商品到购物车")
    public ApiResponse<Cart> addToCart(@RequestBody @Validated Cart cart, HttpServletRequest request) {
        try {
            // 从session中获取用户ID
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            cart.setUserId(userId);
            
            // 验证必要参数
            if (cart.getItemId() == null || cart.getSkuId() == null) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "商品ID和SKU ID不能为空");
            }
            
            Cart result = cartService.addToCart(cart);
            return ResponseUtils.success(result);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "添加购物车失败");
        }
    }

    /**
     * 获取用户购物车列表
     */
    @GetMapping
    @Operation(summary = "获取用户购物车列表")
    public ApiResponse<List<Cart>> getCartList(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            List<Cart> cartList = cartService.getCartList(userId);
            return ResponseUtils.success(cartList);
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "获取购物车列表失败");
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{cartId}/quantity")
    @Operation(summary = "更新购物车商品数量")
    public ApiResponse<Boolean> updateQuantity(
            @PathVariable @Parameter(description = "购物车ID") Long cartId,
            @RequestParam @Parameter(description = "数量") Integer quantity,
            HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            boolean result = cartService.updateQuantity(cartId, userId, quantity);
            return ResponseUtils.success(result);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "更新数量失败");
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartId}")
    @Operation(summary = "删除购物车商品")
    public ApiResponse<Boolean> removeCartItem(
            @PathVariable @Parameter(description = "购物车ID") Long cartId,
            HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            boolean result = cartService.removeCartItem(cartId, userId);
            return ResponseUtils.success(result);
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "删除失败");
        }
    }

    /**
     * 批量删除购物车商品
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除购物车商品")
    public ApiResponse<Boolean> batchRemoveCartItems(
            @RequestParam @Parameter(description = "购物车ID列表，用逗号分隔") String cartIds,
            HttpServletRequest request) {
        try {
            // 从session中获取用户ID
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            // 解析ID列表
            List<Long> ids = List.of(cartIds.split(","))
                    .stream()
                    .map(Long::valueOf)
                    .collect(java.util.stream.Collectors.toList());
            
            boolean result = cartService.batchRemoveCartItems(ids, userId);
            return ResponseUtils.success(result);
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "批量删除失败");
        }
    }

    /**
     * 更新购物车商品选中状态
     */
    @PutMapping("/selected")
    @Operation(summary = "更新购物车商品选中状态")
    public ApiResponse<Boolean> updateSelectedStatus(
            @RequestParam @Parameter(description = "选中状态，0-未选中，1-选中") Byte selected,
            HttpServletRequest request) {
        try {
            // 从session中获取用户ID
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            boolean result = cartService.updateSelectedStatus(userId, selected);
            return ResponseUtils.success(result);
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "更新选中状态失败");
        }
    }

    /**
     * 获取购物车商品总数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取购物车商品总数量")
    public ApiResponse<Integer> getTotalQuantity(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            int count = cartService.getTotalQuantity(userId);
            return ResponseUtils.success(count);
        } catch (Exception e) {
            return ResponseUtils.success(0);
        }
    }

    /**
     * 清空用户购物车
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空用户购物车")
    public ApiResponse<Boolean> clearCart(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            boolean result = cartService.clearCart(userId);
            return ResponseUtils.success(result);
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "清空购物车失败");
        }
    }
}