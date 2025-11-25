package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("red_packet")
@Schema(description = "红包（商家发布）")
public class RedPacket {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId;

    @TableField("title")
    private String title;

    /** 固定金额红包 */
    @TableField("amount")
    private BigDecimal amount;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("remaining_count")
    private Integer remainingCount;

    /** 每用户可领取次数 */
    @TableField("per_user_limit")
    private Integer perUserLimit;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    /** 状态：1=启用,0=停用 */
    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}