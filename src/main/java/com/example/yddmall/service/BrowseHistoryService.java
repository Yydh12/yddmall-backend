package com.example.yddmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.vo.FootprintItemVO;

public interface BrowseHistoryService {

    boolean record(Long userId, Long itemId, Long skuId);

    Page<FootprintItemVO> list(Long userId, long pageNum, long pageSize);

    boolean remove(Long userId, Long itemId);

    boolean clear(Long userId);
}