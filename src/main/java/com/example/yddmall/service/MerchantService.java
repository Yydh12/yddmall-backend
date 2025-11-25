package com.example.yddmall.service;

import com.example.yddmall.entity.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商家表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-08-29
 */
public interface MerchantService extends IService<Merchant> {

    Merchant add(Merchant merchant);

}
