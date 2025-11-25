package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.yddmall.json.MultiLocalDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
@Schema(description = "平台优惠券（管理员发布）")
public class Coupon {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    /** 1=固定金额，2=折扣百分比 */
    @TableField("discount_type")
    private Integer discountType;

    /** 固定金额或百分比值（如 10.00 元 或 15 表示15%） */
    @TableField("discount_value")
    private BigDecimal discountValue;

    /** 使用门槛金额 */
    @TableField("min_spend")
    private BigDecimal minSpend;

    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = MultiLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = MultiLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("remaining_count")
    private Integer remainingCount;

    /** 每用户可领取次数 */
    @TableField("per_user_limit")
    private Integer perUserLimit;

    /** 状态：1=启用,0=停用 */
    @TableField("status")
    private Integer status;

    @TableField("created_by")
    private Long createdBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = MultiLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = MultiLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}