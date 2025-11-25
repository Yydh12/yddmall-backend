package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yddmall.dto.CommentDTO;
import com.example.yddmall.entity.Comment;
import com.example.yddmall.mapper.CommentMapper;
import com.example.yddmall.service.CommentService;
import com.example.yddmall.vo.CommentVO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public IPage<CommentVO> pageItemComments(Page<CommentVO> page, Long itemId) {
        return this.baseMapper.selectPageByItemId(page, itemId);
    }

    @Override
    public Comment addComment(Long userId, CommentDTO dto) {
        Comment c = new Comment();
        c.setItemId(dto.getItemId());
        c.setSkuId(dto.getSkuId());
        c.setUserId(userId);
        c.setRating(dto.getRating());
        c.setContent(dto.getContent());
        // 保存图片URL（逗号分隔），允许为空
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            c.setImageUrls(String.join(",", dto.getImageUrls()));
        } else {
            c.setImageUrls(null);
        }
        c.setStatus(1);
        this.save(c);
        return c;
    }

    @Override
    public IPage<CommentVO> pageStoreRecent(Page<CommentVO> page, Long sellerId) {
        return this.baseMapper.selectPageBySeller(page, sellerId);
    }

    @Override
    public boolean replyToComment(Long commentId, Long userId, String replyContent) {
        // 简单更新回复内容与时间；如需记录回复用户，可扩展字段
        return this.lambdaUpdate()
                .set(Comment::getReplyContent, replyContent)
                .set(Comment::getReplyTime, LocalDateTime.now())
                .eq(Comment::getCommentId, commentId)
                .update();
    }

    @Override
    public IPage<CommentVO> pageStoreAll(Page<CommentVO> page, Long sellerId) {
        return this.baseMapper.selectPageBySellerAll(page, sellerId);
    }

    @Override
    public boolean updateStatus(Long commentId, Integer status) {
        return this.lambdaUpdate()
                .set(Comment::getStatus, status)
                .eq(Comment::getCommentId, commentId)
                .update();
    }

    @Override
    public boolean deleteComment(Long commentId) {
        return this.removeById(commentId);
    }
}