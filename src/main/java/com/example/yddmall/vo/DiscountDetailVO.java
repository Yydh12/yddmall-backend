package com.example.yddmall.vo;

import com.example.yddmall.entity.Coupon;
import com.example.yddmall.entity.RedPacket;

import java.math.BigDecimal;

public class DiscountDetailVO {
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;

    private Coupon coupon;
    private RedPacket redPacket;

    private Long coinUsed;
    private BigDecimal couponDiscount;
    private BigDecimal redPacketDiscount;
    private BigDecimal coinDiscount;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }

    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }

    public RedPacket getRedPacket() { return redPacket; }
    public void setRedPacket(RedPacket redPacket) { this.redPacket = redPacket; }

    public Long getCoinUsed() { return coinUsed; }
    public void setCoinUsed(Long coinUsed) { this.coinUsed = coinUsed; }

    public BigDecimal getCouponDiscount() { return couponDiscount; }
    public void setCouponDiscount(BigDecimal couponDiscount) { this.couponDiscount = couponDiscount; }

    public BigDecimal getRedPacketDiscount() { return redPacketDiscount; }
    public void setRedPacketDiscount(BigDecimal redPacketDiscount) { this.redPacketDiscount = redPacketDiscount; }

    public BigDecimal getCoinDiscount() { return coinDiscount; }
    public void setCoinDiscount(BigDecimal coinDiscount) { this.coinDiscount = coinDiscount; }
}