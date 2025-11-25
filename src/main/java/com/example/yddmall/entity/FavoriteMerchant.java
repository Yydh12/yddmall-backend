package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "用户收藏店铺表")
public class FavoriteMerchant {

    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long favoriteId;

    private Long userId;

    private Long merchantId;

    private Date createTime;
}