package com.example.yddmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品No")
    private String itemNo;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "评分")
    private Integer rating;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论图片URL（逗号分隔）")
    private String imageUrls;

    @Schema(description = "商品标题")
    private String itemTitle;

    @Schema(description = "商品主图URL（逗号分隔，前端可取第一张）")
    private String itemPicUrl;

    @Schema(description = "商家回复内容")
    private String replyContent;

    @Schema(description = "商家回复时间")
    private LocalDateTime replyTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "评论状态")
    private Integer status;
}