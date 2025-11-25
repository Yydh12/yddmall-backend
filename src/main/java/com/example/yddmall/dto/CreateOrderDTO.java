package com.example.yddmall.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CreateOrderDTO implements Serializable {
    
    private Long addressId;
    
    private Integer payType;
    
    private String buyerMessage;
    
    private Integer orderSource = 1;
    
    private List<Long> cartItemIds;
}