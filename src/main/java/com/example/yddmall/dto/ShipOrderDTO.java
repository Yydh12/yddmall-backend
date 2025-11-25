package com.example.yddmall.dto;

import lombok.Data;

@Data
public class ShipOrderDTO {
    private Long orderId;
    private String logisticsCompany;
    private String logisticsNo;
}