package com.example.yddmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.entity.Merchant;

public interface FavoriteMerchantService {

    boolean addFavorite(Long userId, Long merchantId);

    boolean removeFavorite(Long userId, Long merchantId);

    Page<Merchant> listFavorites(Long userId, long pageNum, long pageSize);

    boolean isFavorite(Long userId, Long merchantId);
}