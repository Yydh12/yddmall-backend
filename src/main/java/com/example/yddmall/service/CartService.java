package com.example.yddmall.service;

import com.example.yddmall.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-10-29
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     * @param cart 购物车信息
     * @return 添加结果
     */
    Cart addToCart(Cart cart);

    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> getCartList(Long userId);

    /**
     * 更新购物车商品数量
     * @param cartId 购物车ID
     * @param userId 用户ID
     * @param quantity 数量
     * @return 更新结果
     */
    boolean updateQuantity(Long cartId, Long userId, Integer quantity);

    /**
     * 删除购物车商品
     * @param cartId 购物车ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean removeCartItem(Long cartId, Long userId);

    /**
     * 批量删除购物车商品
     * @param cartIds 购物车ID列表
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean batchRemoveCartItems(List<Long> cartIds, Long userId);

    /**
     * 更新购物车商品选中状态
     * @param userId 用户ID
     * @param selected 选中状态
     * @return 更新结果
     */
    boolean updateSelectedStatus(Long userId, Byte selected);

    /**
     * 获取购物车商品总数量
     * @param userId 用户ID
     * @return 总数量
     */
    int getTotalQuantity(Long userId);

    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 清空结果
     */
    boolean clearCart(Long userId);
}