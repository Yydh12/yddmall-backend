package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "用户商品浏览足迹表")
public class BrowseHistory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long itemId;

    private Long skuId; // 可选

    private Date visitedAt;
}