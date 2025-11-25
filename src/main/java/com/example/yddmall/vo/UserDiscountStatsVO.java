package com.example.yddmall.vo;

import java.math.BigDecimal;

public class UserDiscountStatsVO {
    /** 可用优惠券数量（过滤有效期与启用状态） */
    private Integer couponCount;
    /** 可用红包余额（过滤有效期与启用状态） */
    private BigDecimal redPacketBalance;

    public Integer getCouponCount() { return couponCount; }
    public void setCouponCount(Integer couponCount) { this.couponCount = couponCount; }

    public BigDecimal getRedPacketBalance() { return redPacketBalance; }
    public void setRedPacketBalance(BigDecimal redPacketBalance) { this.redPacketBalance = redPacketBalance; }
}