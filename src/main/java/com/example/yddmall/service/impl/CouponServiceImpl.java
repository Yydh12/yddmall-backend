package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.Coupon;
import com.example.yddmall.entity.UserCoupon;
import com.example.yddmall.mapper.CouponMapper;
import com.example.yddmall.mapper.UserCouponMapper;
import com.example.yddmall.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Override
    public List<Coupon> listAvailable() {
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<Coupon> qw = new QueryWrapper<>();
        qw.eq("status", 1)
          .gt("remaining_count", 0)
          .le("start_time", now)
          .ge("end_time", now);
        return couponMapper.selectList(qw);
    }

    @Transactional
    @Override
    public boolean claim(Long couponId, Long userId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() == null || coupon.getStatus() != 1) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartTime() != null && now.isBefore(coupon.getStartTime())) return false;
        if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) return false;
        if (coupon.getRemainingCount() != null && coupon.getRemainingCount() <= 0) return false;

        // per-user limit
        Integer limit = coupon.getPerUserLimit();
        if (limit != null && limit > 0) {
            int claimed = userCouponMapper.countUserClaims(couponId, userId);
            if (claimed >= limit) return false;
        }

        // decrease remaining_count
        coupon.setRemainingCount(coupon.getRemainingCount() - 1);
        couponMapper.updateById(coupon);

        // add user_coupon
        UserCoupon uc = new UserCoupon();
        uc.setCouponId(couponId);
        uc.setUserId(userId);
        uc.setStatus(0); // claimed
        uc.setClaimedAt(LocalDateTime.now());
        uc.setCreateTime(LocalDateTime.now());
        userCouponMapper.insert(uc);
        return true;
    }

    @Override
    public List<UserCoupon> listUserCoupons(Long userId) {
        QueryWrapper<UserCoupon> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        return userCouponMapper.selectList(qw);
    }

    @Override
    public List<UserCoupon> listUserCoupons(Long userId, Integer status, String orderNo) {
        QueryWrapper<UserCoupon> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        if (status != null) {
            qw.eq("status", status);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            qw.eq("order_no", orderNo);
        }
        return userCouponMapper.selectList(qw);
    }

    @Override
    @Transactional
    public Coupon create(Coupon coupon, Long adminUserId) {
        if (adminUserId == null) {
            throw new IllegalArgumentException("管理员身份无效");
        }
        if (coupon == null) {
            throw new IllegalArgumentException("优惠券参数不能为空");
        }
        if (coupon.getTitle() == null || coupon.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("优惠券标题不能为空");
        }
        Integer type = coupon.getDiscountType();
        if (type == null || (type != 1 && type != 2)) {
            throw new IllegalArgumentException("discountType必须为1(固定金额)或2(折扣百分比)");
        }
        java.math.BigDecimal dv = coupon.getDiscountValue();
        if (dv == null || dv.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("discountValue必须为正");
        }
        Integer total = coupon.getTotalCount();
        if (total == null || total <= 0) {
            throw new IllegalArgumentException("totalCount必须为正整数");
        }
        if (coupon.getPerUserLimit() == null || coupon.getPerUserLimit() < 0) {
            coupon.setPerUserLimit(1);
        }
        // 时间校验（可选）
        if (coupon.getStartTime() != null && coupon.getEndTime() != null
                && coupon.getEndTime().isBefore(coupon.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }

        // 默认启用
        if (coupon.getStatus() == null) {
            coupon.setStatus(1);
        }
        // 剩余数量初始化
        coupon.setRemainingCount(total);
        // 记录创建人
        coupon.setCreatedBy(adminUserId);

        couponMapper.insert(coupon);
        return couponMapper.selectById(coupon.getId());
    }

    @Override
    public List<Coupon> listAll() {
        return couponMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public Coupon getById(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    @Transactional
    public Coupon update(Coupon coupon) {
        if (coupon == null || coupon.getId() == null) {
            throw new IllegalArgumentException("优惠券ID不能为空");
        }
        Coupon db = couponMapper.selectById(coupon.getId());
        if (db == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        if (coupon.getTitle() != null) db.setTitle(coupon.getTitle());
        if (coupon.getDescription() != null) db.setDescription(coupon.getDescription());
        if (coupon.getDiscountType() != null) {
            Integer t = coupon.getDiscountType();
            if (t != 1 && t != 2) throw new IllegalArgumentException("discountType必须为1或2");
            db.setDiscountType(t);
        }
        if (coupon.getDiscountValue() != null) {
            if (coupon.getDiscountValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("discountValue必须为正");
            }
            db.setDiscountValue(coupon.getDiscountValue());
        }
        if (coupon.getMinSpend() != null) {
            if (coupon.getMinSpend().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("minSpend不能为负");
            }
            db.setMinSpend(coupon.getMinSpend());
        }
        if (coupon.getTotalCount() != null && coupon.getTotalCount() > 0) {
            Integer oldTotal = db.getTotalCount() == null ? 0 : db.getTotalCount();
            Integer newTotal = coupon.getTotalCount();
            db.setTotalCount(newTotal);
            Integer remain = db.getRemainingCount() == null ? oldTotal : db.getRemainingCount();
            int delta = newTotal - oldTotal;
            int newRemain = Math.max(0, Math.min(newTotal, remain + delta));
            db.setRemainingCount(newRemain);
        }
        if (coupon.getPerUserLimit() != null && coupon.getPerUserLimit() >= 1) {
            db.setPerUserLimit(coupon.getPerUserLimit());
        }
        if (coupon.getStartTime() != null) db.setStartTime(coupon.getStartTime());
        if (coupon.getEndTime() != null) {
            if (db.getStartTime() != null && coupon.getEndTime().isBefore(db.getStartTime())) {
                throw new IllegalArgumentException("结束时间不能早于开始时间");
            }
            db.setEndTime(coupon.getEndTime());
        }
        if (coupon.getStatus() != null) db.setStatus(coupon.getStatus());
        couponMapper.updateById(db);
        return couponMapper.selectById(db.getId());
    }

    @Override
    @Transactional
    public boolean setStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("参数无效");
        }
        Coupon db = couponMapper.selectById(id);
        if (db == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }
        db.setStatus(status);
        return couponMapper.updateById(db) > 0;
    }
}