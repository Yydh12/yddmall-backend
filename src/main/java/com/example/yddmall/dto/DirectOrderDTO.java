package com.example.yddmall.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DirectOrderDTO implements Serializable {
    
    private Long addressId;
    
    private Integer payType;
    
    private String buyerMessage;
    
    private Integer orderSource = 1;
    
    private List<OrderItemDTO> orderItems;
    
    @Data
    public static class OrderItemDTO {
        private Long itemId;
        private Long skuId;
        private String productName;
        private String skuName;
        private BigDecimal price;
        private String productImage;
        private Integer quantity;
    }
}