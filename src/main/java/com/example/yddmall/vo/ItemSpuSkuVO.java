package com.example.yddmall.vo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemSpuSkuVO {
    @Schema(description = "商品数字ID，全局唯一")
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    @Schema(description = "商家货流售后信息，JSON字符串格式")
    private String aftersale;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "卖点短描述")
    private String subTitle;

    @Schema(description = "一口价/划线价")
    private BigDecimal price;

    @Schema(description = "主图URL列表，逗号分隔的图片路径字符串")
    private String picUrl;

    @Schema(description = "末级淘宝类目ID")
    private Long cid;

    @Schema(description = "品牌ID")
    private String brand;

    @Schema(description = "1上架2下架3删除")
    private Byte status;

    @Schema(description = "店铺主账号ID")
    private Long sellerId;

    @Schema(description = "SKU数字ID")
    @TableId(value = "sku_id", type = IdType.AUTO)
    private Long skuId;

    @Schema(description = "商家SKU标题")
    private String skuName;

    @Schema(description = "当前售价")
    private BigDecimal skuPrice;

    @Schema(description = "可售库存")
    private Integer quantity;

    @Schema(description = "SKU图片URL")
    private String skuPic;
}
