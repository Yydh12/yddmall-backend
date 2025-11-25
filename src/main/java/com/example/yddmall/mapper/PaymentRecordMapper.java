package com.example.yddmall.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentRecordMapper {

    /**
     * 根据订单编号删除支付记录
     */
    @Delete("DELETE FROM payment_record WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);
}