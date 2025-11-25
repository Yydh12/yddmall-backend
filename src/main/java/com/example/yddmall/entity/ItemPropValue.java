package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品属性值定义表")
public class ItemPropValue {

    @Schema(description = "属性值ID")
        @TableId(value = "vid", type = IdType.AUTO)
    private Long vid;

    @Schema(description = "对应属性ID")
    private Long pid;

    @Schema(description = "属性值名称")
    private String name;

}
