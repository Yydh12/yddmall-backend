package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.BrowseHistory;
import com.example.yddmall.vo.FootprintItemVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {

    @Select("SELECT COUNT(1) FROM browse_history WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT i.item_id AS itemId, i.title AS title, i.price AS price, i.pic_url AS picUrl, h.visited_at AS visitedAt " +
            "FROM browse_history h JOIN item i ON h.item_id = i.item_id " +
            "WHERE h.user_id = #{userId} ORDER BY h.visited_at DESC LIMIT #{offset}, #{pageSize}")
    List<FootprintItemVO> selectItemFootprints(@Param("userId") Long userId,
                                               @Param("offset") int offset,
                                               @Param("pageSize") int pageSize);
}