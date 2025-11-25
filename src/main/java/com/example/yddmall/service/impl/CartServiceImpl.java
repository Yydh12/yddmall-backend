package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yddmall.entity.Cart;
import com.example.yddmall.entity.Item;
import com.example.yddmall.entity.ItemSku;
import com.example.yddmall.mapper.CartMapper;
import com.example.yddmall.mapper.ItemMapper;
import com.example.yddmall.mapper.ItemSkuMapper;
import com.example.yddmall.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-10-29
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartMapper cartMapper;
    private final ItemSkuMapper itemSkuMapper;
    private final ItemMapper itemMapper;

    public CartServiceImpl(CartMapper cartMapper, ItemSkuMapper itemSkuMapper, ItemMapper itemMapper) {
        this.cartMapper = cartMapper;
        this.itemSkuMapper = itemSkuMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public Cart addToCart(Cart cart) {
        // 参数校验
        if (cart.getUserId() == null || cart.getItemId() == null || cart.getSkuId() == null) {
            throw new IllegalArgumentException("用户ID、商品ID和SKU ID不能为空");
        }

        // 检查SKU是否存在且有库存
        ItemSku sku = itemSkuMapper.selectById(cart.getSkuId());
        if (sku == null) {
            throw new RuntimeException("商品SKU不存在");
        }
        if (sku.getQuantity() <= 0) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查购物车是否已存在相同SKU的商品
        Cart existingCart = cartMapper.selectByUserIdAndSkuId(cart.getUserId(), cart.getSkuId());
        if (existingCart != null) {
            // 如果已存在，更新数量
            int newQuantity = existingCart.getQuantity() + (cart.getQuantity() != null ? cart.getQuantity() : 1);
            // 检查库存
            if (newQuantity > sku.getQuantity()) {
                throw new RuntimeException("商品库存不足，当前库存：" + sku.getQuantity());
            }
            existingCart.setQuantity(newQuantity);
            existingCart.setUpdateTime(new Date());
            cartMapper.updateById(existingCart);
            return existingCart;
        }

        // 设置默认值
        if (cart.getQuantity() == null) {
            cart.setQuantity(1);
        }
        if (cart.getSelected() == null) {
            cart.setSelected((byte) 0);
        }
        cart.setStatus((byte) 0);
        cart.setStock(sku.getQuantity());
        cart.setSkuName(sku.getSkuName());
        cart.setPrice(sku.getPrice());
        
        // 如果商品名称为空，使用SKU名称
        if (cart.getProductName() == null) {
            cart.setProductName(sku.getSkuName());
        }
        
        // 设置商品图片 - 优先使用SKU图片，如果没有则使用商品主图
        if (cart.getProductImage() == null) {
            // 首先尝试使用SKU图片
            if (sku.getSkuPic() != null && !sku.getSkuPic().trim().isEmpty()) {
                cart.setProductImage(sku.getSkuPic());
            } else {
                // 如果SKU没有图片，获取商品主图
                Item item = itemMapper.selectById(cart.getItemId());
                if (item != null && item.getPicUrl() != null && !item.getPicUrl().trim().isEmpty()) {
                    // 商品主图可能是逗号分隔的多个URL，取第一个
                    String[] picUrls = item.getPicUrl().split(",");
                    if (picUrls.length > 0) {
                        cart.setProductImage(picUrls[0].trim());
                    }
                }
            }
        }
        
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());

        // 保存到数据库
        cartMapper.insert(cart);
        return cart;
    }

    @Override
    public List<Cart> getCartList(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return cartMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public boolean updateQuantity(Long cartId, Long userId, Integer quantity) {
        if (cartId == null || userId == null || quantity == null || quantity < 1) {
            throw new IllegalArgumentException("参数无效");
        }

        // 检查购物车记录是否存在
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车记录不存在或无权限");
        }

        // 检查库存
        ItemSku sku = itemSkuMapper.selectById(cart.getSkuId());
        if (sku != null && quantity > sku.getQuantity()) {
            throw new RuntimeException("商品库存不足，当前库存：" + sku.getQuantity());
        }

        return cartMapper.updateQuantity(cartId, quantity) > 0;
    }

    @Override
    @Transactional
    public boolean removeCartItem(Long cartId, Long userId) {
        if (cartId == null || userId == null) {
            throw new IllegalArgumentException("参数无效");
        }
        return cartMapper.deleteCartItem(cartId, userId) > 0;
    }

    @Override
    @Transactional
    public boolean batchRemoveCartItems(List<Long> cartIds, Long userId) {
        if (cartIds == null || cartIds.isEmpty() || userId == null) {
            throw new IllegalArgumentException("参数无效");
        }
        // 将ID列表转换为字符串
        String cartIdsStr = cartIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return cartMapper.batchDeleteCartItems(cartIdsStr, userId) > 0;
    }

    @Override
    @Transactional
    public boolean updateSelectedStatus(Long userId, Byte selected) {
        if (userId == null || selected == null) {
            throw new IllegalArgumentException("参数无效");
        }
        return cartMapper.updateSelectedStatus(userId, selected) > 0;
    }

    @Override
    public int getTotalQuantity(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return cartMapper.getTotalQuantityByUserId(userId);
    }

    @Override
    @Transactional
    public boolean clearCart(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        // 改为物理删除，确保数据库不再保留该用户的购物车数据
        return cartMapper.deleteByUserId(userId) > 0;
    }
}