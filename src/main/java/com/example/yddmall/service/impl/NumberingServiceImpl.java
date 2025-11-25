package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.yddmall.entity.Item;
import com.example.yddmall.entity.Merchant;
import com.example.yddmall.entity.User;
import com.example.yddmall.service.ItemService;
import com.example.yddmall.service.MerchantService;
import com.example.yddmall.service.NumberingService;
import com.example.yddmall.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NumberingServiceImpl implements NumberingService {

    private final MerchantService merchantService;
    private final ItemService itemService;
    private final UserService userService;

    public NumberingServiceImpl(MerchantService merchantService, ItemService itemService, UserService userService) {
        this.merchantService = merchantService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Map<String, Integer> backfillAll() {
        Map<String, Integer> result = new HashMap<>();
        result.put("merchantUpdated", backfillMerchantNo());
        result.put("itemUpdated", backfillItemNo());
        result.put("userUpdated", backfillUserNo());
        return result;
    }

    private int backfillMerchantNo() {
        LambdaQueryWrapper<Merchant> w = new LambdaQueryWrapper<>();
        w.isNull(Merchant::getMerchantNo).or().eq(Merchant::getMerchantNo, "");
        List<Merchant> list = merchantService.list(w);
        int cnt = 0;
        for (Merchant m : list) {
            if (m.getMerchantId() == null) continue;
            String no = "M" + String.format("%08d", m.getMerchantId());
            m.setMerchantNo(no);
            merchantService.updateById(m);
            cnt++;
        }
        return cnt;
    }

    private int backfillItemNo() {
        LambdaQueryWrapper<Item> w = new LambdaQueryWrapper<>();
        w.isNull(Item::getItemNo).or().eq(Item::getItemNo, "");
        List<Item> list = itemService.list(w);
        int cnt = 0;
        for (Item i : list) {
            if (i.getItemId() == null) continue;
            String no = "I" + String.format("%010d", i.getItemId());
            i.setItemNo(no);
            itemService.updateById(i);
            cnt++;
        }
        return cnt;
    }

    private int backfillUserNo() {
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        w.isNull(User::getUserNo).or().eq(User::getUserNo, "");
        List<User> list = userService.list(w);
        int cnt = 0;
        for (User u : list) {
            if (u.getUserId() == null) continue;
            String no = "U" + String.format("%09d", u.getUserId());
            u.setUserNo(no);
            userService.updateById(u);
            cnt++;
        }
        return cnt;
    }
}