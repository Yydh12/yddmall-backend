package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "购物车表")
@TableName("cart")
public class Cart implements Serializable {

    @Schema(description = "购物车ID（主键，自增）")
    @TableId(value = "cart_id", type = IdType.ASSIGN_ID)
    private Long cartId;

    @Schema(description = "用户ID（关联用户表）")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "商品ID（关联商品表）")
    @TableField("item_id")
    private Long itemId;

    @Schema(description = "SKU ID（关联商品SKU表）")
    @TableField("sku_id")
    private Long skuId;

    @Schema(description = "商品名称")
    @TableField("product_name")
    private String productName;

    @Schema(description = "SKU名称（规格信息）")
    @TableField("sku_name")
    private String skuName;

    @Schema(description = "商品图片URL")
    @TableField("product_image")
    private String productImage;

    @Schema(description = "商品价格")
    @TableField("price")
    private BigDecimal price;

    @Schema(description = "商品数量")
    @TableField("quantity")
    private Integer quantity;

    @Schema(description = "商品库存（冗余字段，用于校验）")
    @TableField("stock")
    private Integer stock;

    @Schema(description = "是否选中（0-未选中，1-选中）")
    @TableField("selected")
    private Byte selected;

    @Schema(description = "创建时间（自动填充）")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间（自动填充）")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "状态（0-正常，1-已删除）")
    @TableField("status")
    private Byte status;
}
