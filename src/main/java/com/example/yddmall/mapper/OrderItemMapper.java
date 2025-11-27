package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.OrderItem;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.vo.PopularItemStatVO;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID查询订单项列表
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} ORDER BY order_item_id ASC")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单编号查询订单项列表
     */
    @Select("SELECT * FROM order_item WHERE order_no = #{orderNo} ORDER BY order_item_id ASC")
    List<OrderItem> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单编号删除订单项
     */
    @Delete("DELETE FROM order_item WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 批量插入订单项
     */
    @Insert("<script>" +
            "INSERT INTO order_item (order_id, order_no, item_id, sku_id, item_name, sku_name, item_pic, price, quantity, total_amount) " +
            "VALUES " +
            "<foreach collection='orderItems' item='item' separator=','>" +
            "(#{item.orderId}, #{item.orderNo}, #{item.itemId}, #{item.skuId}, #{item.itemName}, #{item.skuName}, #{item.itemPic}, #{item.price}, #{item.quantity}, #{item.totalAmount})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);

    /**
     * 店铺热门商品（按销量聚合）分页查询
     */
    @Select("<script>"
            + "SELECT oi.item_id AS itemId, SUM(oi.quantity) AS salesCount "
            + "FROM order_item oi "
            + "JOIN item i ON oi.item_id = i.item_id "
            + "<where>"
            + "i.seller_id = #{sellerId} "
            + "<if test='status != null'> AND i.status = #{status} </if>"
            + "<if test='title != null and title != \"\"'> AND i.title LIKE '%' || #{title} || '%' </if>"
            + "</where>"
            + "GROUP BY oi.item_id "
            + "ORDER BY salesCount DESC"
            + "</script>")
    IPage<PopularItemStatVO> selectPopularItems(Page<PopularItemStatVO> page,
                                               @Param("sellerId") Long sellerId,
                                               @Param("status") Integer status,
                                               @Param("title") String title);
}
