package com.example.yddmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户已领取的优惠券（携带券详细信息）
 */
public class UserCouponVO {
    // user_coupon 原始字段
    private Long id;
    private Long couponId;
    private Long userId;
    /** 0=已领取,1=已使用,2=已过期 */
    private Integer status;
    private LocalDateTime claimedAt;
    private LocalDateTime usedAt;
    private String orderNo;

    // coupon 详情字段
    private String title;
    private String description;
    /** 1=固定金额，2=折扣百分比 */
    private Integer discountType;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDiscountType() { return discountType; }
    public void setDiscountType(Integer discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinSpend() { return minSpend; }
    public void setMinSpend(BigDecimal minSpend) { this.minSpend = minSpend; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}