package com.example.yddmall.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CommentDTO implements Serializable {
    private Long itemId;
    private Long skuId; // 可选
    private Integer rating; // 1-5
    private String content; // 文本内容
    // 评论图片URL列表（发布后保存的访问URL）
    private List<String> imageUrls;
}