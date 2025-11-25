package com.example.yddmall.dto;

public class ApplyDiscountDTO {
    private String orderNo;
    private Long couponId; // optional
    private Long redPacketId; // optional
    private Long coinAmount; // coins to use, optional

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getRedPacketId() {
        return redPacketId;
    }

    public void setRedPacketId(Long redPacketId) {
        this.redPacketId = redPacketId;
    }

    public Long getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(Long coinAmount) {
        this.coinAmount = coinAmount;
    }
}