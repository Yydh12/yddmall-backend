package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.entity.Comment;
import com.example.yddmall.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.comment_id, c.item_id, c.sku_id, c.user_id, c.rating, c.content, c.image_urls AS imageUrls, c.reply_content, c.reply_time, c.create_time, " +
            "u.username AS user_name, u.avatar, i.title AS item_title, i.pic_url AS item_pic_url " +
            "FROM item_comments c " +
            "LEFT JOIN user u ON c.user_id = u.user_id " +
            "LEFT JOIN item i ON c.item_id = i.item_id " +
            "WHERE c.item_id = #{itemId} AND c.status = 1 ORDER BY c.create_time DESC")
    IPage<CommentVO> selectPageByItemId(Page<CommentVO> page, @Param("itemId") Long itemId);

    @Select("SELECT c.comment_id, c.item_id, i.item_no AS itemNo, c.sku_id, c.user_id, c.rating, c.content, c.image_urls AS imageUrls, c.reply_content, c.reply_time, c.create_time, " +
            "u.username AS user_name, u.avatar, i.title AS item_title, i.pic_url AS item_pic_url " +
            "FROM item_comments c " +
            "LEFT JOIN user u ON c.user_id = u.user_id " +
            "LEFT JOIN item i ON c.item_id = i.item_id " +
            "WHERE i.seller_id = #{sellerId} AND c.status = 1 ORDER BY c.create_time DESC")
    IPage<CommentVO> selectPageBySeller(Page<CommentVO> page, @Param("sellerId") Long sellerId);

    @Select("<script>" +
            "SELECT c.comment_id, c.item_id, i.item_no AS itemNo, c.sku_id, c.user_id, c.rating, c.content, c.image_urls AS imageUrls, IFNULL(c.reply_content, '') AS reply_content, IFNULL(c.reply_time, NULL) AS reply_time, c.create_time, c.status, " +
            "u.username AS user_name, u.avatar, i.title AS item_title, i.pic_url AS item_pic_url " +
            "FROM item_comments c " +
            "LEFT JOIN user u ON c.user_id = u.user_id " +
            "LEFT JOIN item i ON c.item_id = i.item_id " +
            "<where>" +
            "<if test=\"sellerId != null\">i.seller_id = #{sellerId}</if>" +
            "</where>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<CommentVO> selectPageBySellerAll(Page<CommentVO> page, @Param("sellerId") Long sellerId);

    @Select("SELECT AVG(c.rating) FROM item_comments c LEFT JOIN item i ON c.item_id = i.item_id WHERE i.seller_id = #{sellerId} AND c.status = 1")
    Double getAvgRatingBySeller(@Param("sellerId") Long sellerId);

    @Select("SELECT CASE WHEN COUNT(*) = 0 THEN NULL ELSE SUM(CASE WHEN c.rating >= 4 THEN 1 ELSE 0 END) * 1.0 / COUNT(*) END FROM item_comments c LEFT JOIN item i ON c.item_id = i.item_id WHERE i.seller_id = #{sellerId} AND c.status = 1")
    Double getPositiveRateBySeller(@Param("sellerId") Long sellerId);
}