package com.example.yddmall.entity;

    import com.baomidou.mybatisplus.annotation.TableId;
    import java.math.BigDecimal;
    import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品SKU信息表")
public class ItemSku {

    @Schema(description = "SKU数字ID")
        @TableId(value = "sku_id", type = IdType.AUTO)
    private Long skuId;

    @Schema(description = "对应item表的商品ID")
    private Long itemId;

    @Schema(description = "商家SKU标题")
    private String skuName;

    @Schema(description = "当前售价")
    private BigDecimal price;

    @Schema(description = "可售库存")
    private Integer quantity;

    @Schema(description = "SKU图片URL")
    private String skuPic;

    @Schema(description = "1正常2停售")
    private Byte status;

}
