package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.entity.Coupon;
import com.example.yddmall.entity.UserCoupon;
import com.example.yddmall.service.CouponService;
import com.example.yddmall.service.UserService;
import com.example.yddmall.entity.User;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.vo.UserCouponVO;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Resource
    private CouponService couponService;

    @Resource
    private UserService userService;

    @GetMapping("/available")
    public ApiResponse<List<Coupon>> listAvailable() {
        return ResponseUtils.success(couponService.listAvailable());
    }

    @PostMapping("/claim/{couponId}")
    public ApiResponse<Boolean> claim(@PathVariable Long couponId, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        boolean ok = couponService.claim(couponId, userId);
        if (ok) return ResponseUtils.success(true);
        return ResponseUtils.error(ResponseCode.BAD_REQUEST, "领取失败或不满足条件");
    }

    @GetMapping("/mine")
    public ApiResponse<List<UserCouponVO>> mine(
            HttpServletRequest request,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "orderNo", required = false) String orderNo
    ) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }
        List<UserCoupon> list;
        if (status == null && (orderNo == null || orderNo.isEmpty())) {
            list = couponService.listUserCoupons(userId);
        } else {
            list = couponService.listUserCoupons(userId, status, orderNo);
        }
        // enrich with coupon details
        java.util.List<UserCouponVO> voList = new java.util.ArrayList<>();
        for (UserCoupon uc : list) {
            if (uc == null) continue;
            UserCouponVO vo = new UserCouponVO();
            vo.setId(uc.getId());
            vo.setCouponId(uc.getCouponId());
            vo.setUserId(uc.getUserId());
            vo.setStatus(uc.getStatus());
            vo.setClaimedAt(uc.getClaimedAt());
            vo.setUsedAt(uc.getUsedAt());
            vo.setOrderNo(uc.getOrderNo());
            if (uc.getCouponId() != null) {
                Coupon c = couponService.getById(uc.getCouponId());
                if (c != null) {
                    vo.setTitle(c.getTitle());
                    vo.setDescription(c.getDescription());
                    vo.setDiscountType(c.getDiscountType());
                    vo.setDiscountValue(c.getDiscountValue());
                    vo.setMinSpend(c.getMinSpend());
                    vo.setStartTime(c.getStartTime());
                    vo.setEndTime(c.getEndTime());
                }
            }
            voList.add(vo);
        }
        return ResponseUtils.success(voList);
    }

    /** 管理员发布平台优惠券 */
    @PostMapping("/create")
    public ApiResponse<Coupon> create(@RequestBody Coupon coupon, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null || user.getRoleId() != 1L) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员可发布平台优惠券");
        }
        try {
            Coupon created = couponService.create(coupon, userId);
            return ResponseUtils.success(created);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "发布失败: " + e.getMessage());
        }
    }

    /** 管理员：查询所有优惠券 */
    @GetMapping("/all")
    public ApiResponse<List<Coupon>> listAll(HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null || user.getRoleId() != 1L) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员可查看");
        }
        return ResponseUtils.success(couponService.listAll());
    }

    /** 管理员：更新优惠券 */
    @PutMapping("/update")
    public ApiResponse<Coupon> update(@RequestBody Coupon coupon, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null || user.getRoleId() != 1L) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员可操作");
        }
        if (coupon == null || coupon.getId() == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "ID不能为空");
        }
        // 防止越权修改remaining_count、createdBy等关键字段（实现中已忽略未传字段）
        coupon.setRemainingCount(null);
        coupon.setCreatedBy(null);
        try {
            Coupon updated = couponService.update(coupon);
            return ResponseUtils.success(updated);
        } catch (IllegalArgumentException e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "更新失败: " + e.getMessage());
        }
    }

    /** 管理员：启用优惠券 */
    @PostMapping("/enable/{id}")
    public ApiResponse<Boolean> enable(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null || user.getRoleId() != 1L) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员可操作");
        }
        boolean ok = couponService.setStatus(id, 1);
        return ok ? ResponseUtils.success(true) : ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "操作失败");
    }

    /** 管理员：停用优惠券 */
    @PostMapping("/disable/{id}")
    public ApiResponse<Boolean> disable(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "身份校验失败，请重新登录");
        }
        User user = userService.getById(userId);
        if (user == null || user.getRoleId() == null || user.getRoleId() != 1L) {
            return ResponseUtils.error(ResponseCode.FORBIDDEN, "仅管理员可操作");
        }
        boolean ok = couponService.setStatus(id, 0);
        return ok ? ResponseUtils.success(true) : ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "操作失败");
    }
}
