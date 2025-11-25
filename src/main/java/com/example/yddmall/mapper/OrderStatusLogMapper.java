package com.example.yddmall.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderStatusLogMapper {

    /**
     * 根据订单编号删除状态变更记录
     */
    @Delete("DELETE FROM order_status_log WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);
}