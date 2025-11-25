package com.example.yddmall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yddmall.dto.CommentDTO;
import com.example.yddmall.entity.Comment;
import com.example.yddmall.vo.CommentVO;

public interface CommentService extends IService<Comment> {
    IPage<CommentVO> pageItemComments(Page<CommentVO> page, Long itemId);
    Comment addComment(Long userId, CommentDTO dto);
    IPage<CommentVO> pageStoreRecent(Page<CommentVO> page, Long sellerId);
    boolean replyToComment(Long commentId, Long userId, String replyContent);
    IPage<CommentVO> pageStoreAll(Page<CommentVO> page, Long sellerId);
    boolean updateStatus(Long commentId, Integer status);
    boolean deleteComment(Long commentId);
}