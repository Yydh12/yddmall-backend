package com.example.yddmall.service;

import com.example.yddmall.entity.Coupon;
import com.example.yddmall.entity.UserCoupon;

import java.util.List;

public interface CouponService {
    List<Coupon> listAvailable();
    boolean claim(Long couponId, Long userId);
    List<UserCoupon> listUserCoupons(Long userId);
    List<UserCoupon> listUserCoupons(Long userId, Integer status, String orderNo);
    /** 管理员发布平台优惠券 */
    Coupon create(Coupon coupon, Long adminUserId);
    /** 管理端：查询全部优惠券 */
    List<Coupon> listAll();
    /** 管理端：根据ID获取优惠券 */
    Coupon getById(Long id);
    /** 管理端：更新优惠券 */
    Coupon update(Coupon coupon);
    /** 管理端：启停优惠券（status=1启用，0停用） */
    boolean setStatus(Long id, Integer status);
}