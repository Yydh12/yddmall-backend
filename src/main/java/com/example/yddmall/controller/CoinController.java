package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.entity.UserCoinWallet;
import com.example.yddmall.service.UserCoinWalletService;
import com.example.yddmall.utils.SessionUserUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/coin")
public class CoinController {

    @Resource
    private UserCoinWalletService walletService;

    @GetMapping("/wallet")
    public ApiResponse<UserCoinWallet> wallet(HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        return ResponseUtils.success(walletService.getOrCreate(userId));
    }

    @PostMapping("/signin")
    public ApiResponse<UserCoinWallet> signin(HttpServletRequest request, @RequestParam(value = "coins", required = false) Long coins) {
        Long userId = SessionUserUtils.getUserId(request);
        long add = coins != null ? coins : 10L; // default daily sign-in reward
        return ResponseUtils.success(walletService.dailySignIn(userId, add));
    }
}