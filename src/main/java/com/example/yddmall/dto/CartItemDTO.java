package com.example.yddmall.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartItemDTO implements Serializable {
    
    private Long itemId;
    
    private Long skuId;
    
    private String productName;
    
    private String skuName;
    
    private BigDecimal price;
    
    private String productImage;
    
    private Integer quantity;
    
    private Integer stock;
}