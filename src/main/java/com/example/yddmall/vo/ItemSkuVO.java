package com.example.yddmall.vo;


import java.math.BigDecimal;
import lombok.Data;

@Data
public class ItemSkuVO {
    private Long itemId;       // 同skuId
    private String itemNo;     // 商品编号（外部展示用）
    private String title;    // 用title
    private String subtitle;    // 用title
    private String brand;
    private BigDecimal price;
    private Integer quantity;  // 默认0或从库存服务查
    private String skuPic;     // 取picUrl第一个
    private Integer status;    // 用item.status
    private Integer sellerId;   // 店铺状态
    private Long salesCount;    // 销量统计（用于热门排序）
}
