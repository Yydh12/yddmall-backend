package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.entity.FavoriteMerchant;
import com.example.yddmall.entity.Merchant;
import com.example.yddmall.mapper.FavoriteMerchantMapper;
import com.example.yddmall.service.FavoriteMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FavoriteMerchantServiceImpl implements FavoriteMerchantService {

    @Autowired
    private FavoriteMerchantMapper favoriteMerchantMapper;

    @Override
    public boolean addFavorite(Long userId, Long merchantId) {
        FavoriteMerchant fm = new FavoriteMerchant();
        fm.setUserId(userId);
        fm.setMerchantId(merchantId);
        fm.setCreateTime(new Date());
        try {
            // 插入，若有唯一约束会抛错；可先尝试存在性检查
            // 由于没有自然主键组合，这里直接插入；重复数据可按唯一键在SQL层约束
            favoriteMerchantMapper.insert(fm);
            return true;
        } catch (Exception e) {
            // 已存在则视为成功
            return true;
        }
    }

    @Override
    public boolean removeFavorite(Long userId, Long merchantId) {
        return favoriteMerchantMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<FavoriteMerchant>()
                        .eq("user_id", userId)
                        .eq("merchant_id", merchantId)
        ) > 0;
    }

    @Override
    public Page<Merchant> listFavorites(Long userId, long pageNum, long pageSize) {
        long total = favoriteMerchantMapper.countByUserId(userId);
        int offset = (int) ((pageNum - 1) * pageSize);
        List<Merchant> records = favoriteMerchantMapper.selectMerchantsByUser(userId, offset, (int) pageSize);
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.setRecords(records);
        return page;
    }

    @Override
    public boolean isFavorite(Long userId, Long merchantId) {
        return favoriteMerchantMapper.existsByUserAndMerchant(userId, merchantId) > 0;
    }
}