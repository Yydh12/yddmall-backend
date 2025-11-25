package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {
    
    @TableId(value = "order_item_id", type = IdType.AUTO)
    private Long orderItemId;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("item_id")
    private Long itemId;
    
    @TableField("sku_id")
    private Long skuId;
    
    @TableField("item_name")
    private String itemName;
    
    @TableField("sku_name")
    private String skuName;
    
    @TableField("item_pic")
    private String itemPic;
    
    @TableField("price")
    private BigDecimal price;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}