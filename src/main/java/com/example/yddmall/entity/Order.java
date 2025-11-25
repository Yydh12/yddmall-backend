package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("address_id")
    private Long addressId;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField("pay_amount")
    private BigDecimal payAmount;
    
    @TableField("freight_amount")
    private BigDecimal freightAmount;
    
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    @TableField("pay_type")
    private Integer payType;
    
    @TableField("order_status")
    private Integer orderStatus;
    
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    @TableField("delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;
    
    @TableField("receive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;
    
    @TableField("receiver_name")
    private String receiverName;
    
    @TableField("receiver_phone")
    private String receiverPhone;
    
    @TableField("receiver_address")
    private String receiverAddress;
    
    @TableField("buyer_message")
    private String buyerMessage;
    
    @TableField("remark")
    private String remark;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private List<OrderItem> orderItems;
    
    // 订单状态枚举
    public static class OrderStatus {
        public static final int PENDING_PAYMENT = 0; // 待支付
        public static final int PAID = 1; // 已支付
        public static final int SHIPPED = 2; // 已发货
        public static final int COMPLETED = 3; // 已完成
        public static final int CANCELLED = 4; // 已取消
        public static final int REFUND_APPLIED = 5; // 申请中（退款申请）
    }
    
    // 支付方式枚举
    public static class PayType {
        public static final int ALIPAY = 1; // 支付宝
        public static final int WECHAT = 2; // 微信
        public static final int BANK_CARD = 3; // 银行卡
    }
}