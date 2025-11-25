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
@TableName("user_red_packet")
@Schema(description = "用户领取的红包")
public class UserRedPacket {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("red_packet_id")
    private Long redPacketId;

    @TableField("user_id")
    private Long userId;

    /** 0=已领取,1=已使用,2=已过期 */
    @TableField("status")
    private Integer status;

    @TableField("claimed_at")
    private LocalDateTime claimedAt;

    @TableField("used_at")
    private LocalDateTime usedAt;

    @TableField("order_no")
    private String orderNo;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}