package com.example.yddmall.service;

import com.example.yddmall.entity.UserCoinWallet;

public interface UserCoinWalletService {
    UserCoinWallet getOrCreate(Long userId);
    UserCoinWallet dailySignIn(Long userId, long coinsToAdd);
}