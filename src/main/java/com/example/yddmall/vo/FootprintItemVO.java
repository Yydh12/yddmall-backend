package com.example.yddmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FootprintItemVO {
    private Long itemId;
    private String title;
    private BigDecimal price;
    private String picUrl;
    private Date visitedAt;
}