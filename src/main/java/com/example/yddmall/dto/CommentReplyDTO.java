package com.example.yddmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentReplyDTO {
    @Schema(description = "回复内容")
    private String replyContent;
}