package com.example.yddmall.service.impl;

import com.example.yddmall.entity.UserCoinWallet;
import com.example.yddmall.mapper.UserCoinWalletMapper;
import com.example.yddmall.service.UserCoinWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserCoinWalletServiceImpl implements UserCoinWalletService {

    @Resource
    private UserCoinWalletMapper walletMapper;

    @Override
    public UserCoinWallet getOrCreate(Long userId) {
        UserCoinWallet wallet = walletMapper.findByUserId(userId);
        if (wallet == null) {
            wallet = new UserCoinWallet();
            wallet.setUserId(userId);
            wallet.setBalance(0L);
            wallet.setCreateTime(LocalDateTime.now());
            walletMapper.insert(wallet);
        }
        return wallet;
    }

    @Transactional
    @Override
    public UserCoinWallet dailySignIn(Long userId, long coinsToAdd) {
        UserCoinWallet wallet = getOrCreate(userId);
        LocalDate today = LocalDate.now();
        if (wallet.getLastSignTime() != null && wallet.getLastSignTime().toLocalDate().isEqual(today)) {
            return wallet; // already signed in today
        }
        wallet.setBalance(wallet.getBalance() + coinsToAdd);
        wallet.setLastSignTime(LocalDateTime.now());
        wallet.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(wallet);
        return wallet;
    }
}