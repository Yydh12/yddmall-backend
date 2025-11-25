package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_coin_wallet")
@Schema(description = "用户金币钱包")
public class UserCoinWallet {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    /** 金币余额（整数，单位：枚） */
    @TableField("balance")
    private Long balance;

    /** 最后签到日期时间（用于每日签到限制） */
    @TableField("last_sign_time")
    private LocalDateTime lastSignTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}