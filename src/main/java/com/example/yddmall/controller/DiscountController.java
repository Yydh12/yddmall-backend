package com.example.yddmall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.entity.Coupon;
import com.example.yddmall.entity.UserCoupon;
import com.example.yddmall.entity.RedPacket;
import com.example.yddmall.entity.UserRedPacket;
import com.example.yddmall.mapper.CouponMapper;
import com.example.yddmall.mapper.UserCouponMapper;
import com.example.yddmall.mapper.RedPacketMapper;
import com.example.yddmall.mapper.UserRedPacketMapper;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.vo.UserDiscountStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/discount")
public class DiscountController {

    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private CouponMapper couponMapper;
    @Resource
    private UserRedPacketMapper userRedPacketMapper;
    @Resource
    private RedPacketMapper redPacketMapper;

    /**
     * 用户优惠资产统计：优惠券数量、红包余额
     */
    @GetMapping("/stats")
    public ApiResponse<UserDiscountStatsVO> getStats(HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }

        LocalDateTime now = LocalDateTime.now();

        // 统计可用优惠券数量（用户已领取、未使用、券启用且在有效期）
        QueryWrapper<UserCoupon> couponQw = new QueryWrapper<>();
        couponQw.eq("user_id", userId).eq("status", 0);
        List<UserCoupon> myCoupons = userCouponMapper.selectList(couponQw);
        int validCouponCount = 0;
        for (UserCoupon uc : myCoupons) {
            if (uc == null || uc.getCouponId() == null) continue;
            Coupon c = couponMapper.selectById(uc.getCouponId());
            if (c == null) continue;
            boolean enabled = c.getStatus() != null && c.getStatus() == 1;
            boolean inWindow = (c.getStartTime() == null || !now.isBefore(c.getStartTime()))
                    && (c.getEndTime() == null || !now.isAfter(c.getEndTime()));
            if (enabled && inWindow) {
                validCouponCount++;
            }
        }

        // 统计可用红包余额（用户已领取、未使用、红包启用且在有效期）
        QueryWrapper<UserRedPacket> rpQw = new QueryWrapper<>();
        rpQw.eq("user_id", userId).eq("status", 0);
        List<UserRedPacket> myRedPackets = userRedPacketMapper.selectList(rpQw);
        BigDecimal redPacketBalance = BigDecimal.ZERO;
        for (UserRedPacket urp : myRedPackets) {
            if (urp == null || urp.getRedPacketId() == null) continue;
            RedPacket rp = redPacketMapper.selectById(urp.getRedPacketId());
            if (rp == null) continue;
            boolean enabled = rp.getStatus() != null && rp.getStatus() == 1;
            boolean inWindow = (rp.getStartTime() == null || !now.isBefore(rp.getStartTime()))
                    && (rp.getEndTime() == null || !now.isAfter(rp.getEndTime()));
            if (enabled && inWindow) {
                BigDecimal amt = rp.getAmount() != null ? rp.getAmount() : BigDecimal.ZERO;
                redPacketBalance = redPacketBalance.add(amt);
            }
        }

        UserDiscountStatsVO vo = new UserDiscountStatsVO();
        vo.setCouponCount(validCouponCount);
        vo.setRedPacketBalance(redPacketBalance);
        return ResponseUtils.success(vo);
    }
}