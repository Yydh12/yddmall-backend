package com.example.yddmall.service;

import java.util.Map;

public interface NumberingService {
    /**
     * 回填商家、商品、用户编号，返回各表更新条数。
     */
    Map<String, Integer> backfillAll();
}