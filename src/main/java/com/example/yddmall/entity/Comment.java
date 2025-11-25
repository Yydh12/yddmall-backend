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
@TableName("item_comments")
@Schema(description = "商品评论表")
public class Comment {

    @Schema(description = "评论ID（主键，自增）")
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    @Schema(description = "商品ID")
    @TableField("item_id")
    private Long itemId;

    @Schema(description = "SKU ID（可空）")
    @TableField("sku_id")
    private Long skuId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "评分（1-5）")
    @TableField("rating")
    private Integer rating;

    @Schema(description = "评论内容")
    @TableField("content")
    private String content;

    @Schema(description = "评论图片URL（逗号分隔）")
    @TableField("image_urls")
    private String imageUrls;

    @Schema(description = "商家回复内容")
    @TableField("reply_content")
    private String replyContent;

    @Schema(description = "商家回复时间")
    @TableField("reply_time")
    private LocalDateTime replyTime;

    @Schema(description = "状态（1-正常，0-隐藏）")
    @TableField("status")
    private Integer status = 1;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}