package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 购物车表 Mapper 接口
 * </p>
 *
 * @author Yy
 * @since 2025-10-29
 */
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户ID查询购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Cart> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和商品SKU ID查询购物车记录
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车记录
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND sku_id = #{skuId} LIMIT 1")
    Cart selectByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 更新购物车商品数量
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 更新结果
     */
    @Update("UPDATE cart SET quantity = #{quantity}, update_time = NOW() WHERE cart_id = #{cartId}")
    int updateQuantity(@Param("cartId") Long cartId, @Param("quantity") Integer quantity);

    /**
     * 批量更新选中状态
     * @param userId 用户ID
     * @param selected 选中状态
     * @return 更新结果
     */
    @Update("UPDATE cart SET selected = #{selected}, update_time = NOW() WHERE user_id = #{userId}")
    int updateSelectedStatus(@Param("userId") Long userId, @Param("selected") Byte selected);

    /**
     * 物理删除购物车商品
     * @param cartId 购物车ID
     * @param userId 用户ID
     * @return 更新结果
     */
    @Delete("DELETE FROM cart WHERE cart_id = #{cartId} AND user_id = #{userId}")
    int deleteCartItem(@Param("cartId") Long cartId, @Param("userId") Long userId);

    /**
     * 批量删除购物车商品
     * @param cartIds 购物车ID列表
     * @param userId 用户ID
     * @return 更新结果
     */
    @Delete("DELETE FROM cart WHERE cart_id IN (${cartIds}) AND user_id = #{userId}")
    int batchDeleteCartItems(@Param("cartIds") String cartIds, @Param("userId") Long userId);

    /**
     * 根据用户ID清空购物车（物理删除）
     * @param userId 用户ID
     * @return 删除行数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 获取购物车中商品的总数量
     * @param userId 用户ID
     * @return 总数量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = #{userId}")
    int getTotalQuantityByUserId(@Param("userId") Long userId);
}