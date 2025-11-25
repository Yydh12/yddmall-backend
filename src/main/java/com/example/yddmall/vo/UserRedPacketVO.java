package com.example.yddmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户已领取的红包（携带红包详细信息）
 */
public class UserRedPacketVO {
    // user_red_packet 原始字段
    private Long id;
    private Long redPacketId;
    private Long userId;
    /** 0=已领取,1=已使用,2=已过期 */
    private Integer status;
    private LocalDateTime claimedAt;
    private LocalDateTime usedAt;
    private String orderNo;

    // red_packet 详情字段
    private String title;
    private String description;
    private BigDecimal amount;
    /** 使用门槛（可能为 threshold 或 minSpend） */
    private BigDecimal threshold;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long merchantId;
    private String merchantName;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRedPacketId() { return redPacketId; }
    public void setRedPacketId(Long redPacketId) { this.redPacketId = redPacketId; }
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
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getThreshold() { return threshold; }
    public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
}