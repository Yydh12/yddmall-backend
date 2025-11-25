package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.entity.BrowseHistory;
import com.example.yddmall.mapper.BrowseHistoryMapper;
import com.example.yddmall.service.BrowseHistoryService;
import com.example.yddmall.vo.FootprintItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @Override
    public boolean record(Long userId, Long itemId, Long skuId) {
        try {
            BrowseHistory h = new BrowseHistory();
            h.setUserId(userId);
            h.setItemId(itemId);
            h.setSkuId(skuId);
            h.setVisitedAt(new Date());
            browseHistoryMapper.insert(h);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<FootprintItemVO> list(Long userId, long pageNum, long pageSize) {
        long total = browseHistoryMapper.countByUserId(userId);
        int offset = (int) ((pageNum - 1) * pageSize);
        List<FootprintItemVO> records = browseHistoryMapper.selectItemFootprints(userId, offset, (int) pageSize);
        Page<FootprintItemVO> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.setRecords(records);
        return page;
    }

    @Override
    public boolean remove(Long userId, Long itemId) {
        return browseHistoryMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BrowseHistory>()
                        .eq("user_id", userId)
                        .eq("item_id", itemId)
        ) > 0;
    }

    @Override
    public boolean clear(Long userId) {
        return browseHistoryMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BrowseHistory>()
                        .eq("user_id", userId)
        ) > 0;
    }
}