package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商品属性定义表")
public class ItemProp {

    @Schema(description = "属性ID")
        @TableId(value = "pid", type = IdType.AUTO)
    private Long pid;

    @Schema(description = "属性名称")
    private String name;

    @Schema(description = "是否关键属性，1-是，0-否")
    private Byte isKeyProp;

    @Schema(description = "是否销售属性，1-是，0-否")
    private Byte isSaleProp;

    @TableField(exist = false)
    @Schema(description = "item_prop_value表（非数据库字段）")
    private List<ItemPropValue> itemPropValue;

}
