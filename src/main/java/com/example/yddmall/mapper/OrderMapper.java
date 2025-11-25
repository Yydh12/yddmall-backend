package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据用户ID分页查询订单列表（可选状态过滤）
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND (#{orderStatus} IS NULL OR order_status = #{orderStatus}) ORDER BY create_time DESC")
    IPage<Order> selectPageByUserIdAndStatus(Page<Order> page, @Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);
    
    /**
     * 根据订单编号查询订单
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单编号删除订单
     */
    @Delete("DELETE FROM orders WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND order_status = #{orderStatus} ORDER BY create_time DESC")
    List<Order> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);
    
    /**
     * 统计用户的订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户不同状态的订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId} AND order_status = #{orderStatus}")
    int countByUserIdAndStatus(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);
}