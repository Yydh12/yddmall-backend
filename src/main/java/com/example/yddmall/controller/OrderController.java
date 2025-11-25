package com.example.yddmall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.dto.CreateOrderDTO;
import com.example.yddmall.dto.DirectOrderDTO;
import com.example.yddmall.dto.ApplyDiscountDTO;
import com.example.yddmall.dto.ShipOrderDTO;
import com.example.yddmall.entity.Order;
import com.example.yddmall.service.OrderService;
import com.example.yddmall.vo.DiscountDetailVO;
import com.example.yddmall.utils.SessionUserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单（通过购物车）
     */
    @PostMapping("/create")
    public ApiResponse<Order> createOrder(@RequestBody CreateOrderDTO createOrderDTO, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            Order order = orderService.createOrder(createOrderDTO, userId);
            return new ApiResponse<>(200, "创建订单成功", order);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 创建订单（直接购买，不经过购物车）
     */
    @PostMapping("/createDirect")
    public ApiResponse<Order> createDirectOrder(@RequestBody DirectOrderDTO directOrderDTO, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            Order order = orderService.createDirectOrder(directOrderDTO, userId);
            return new ApiResponse<>(200, "创建订单成功", order);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public ApiResponse<Order> getOrderDetail(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            Order order = orderService.getOrderWithItems(orderNo, userId);
            if (order == null) {
                return new ApiResponse<>(404, "订单不存在", null);
            }
            return new ApiResponse<>(200, "获取成功", order);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 获取订单的优惠明细
     */
    @GetMapping("/discount/detail/{orderNo}")
    public ApiResponse<DiscountDetailVO> getOrderDiscountDetail(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            DiscountDetailVO detail = orderService.getOrderDiscountDetail(orderNo, userId);
            if (detail == null) {
                return new ApiResponse<>(404, "订单不存在", null);
            }
            return new ApiResponse<>(200, "获取成功", detail);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public ApiResponse<IPage<Order>> getUserOrders(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status,
            HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            int p = page != null ? page : (pageNum != null ? pageNum : 1);
            int s = size != null ? size : (pageSize != null ? pageSize : 10);
            Page<Order> pageParam = new Page<>(p, s);
            IPage<Order> orderPage = orderService.getUserOrders(pageParam, userId, status);
            return new ApiResponse<>(200, "获取成功", orderPage);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 根据状态获取订单列表
     */
    @GetMapping("/list/{status}")
    public ApiResponse<List<Order>> getOrdersByStatus(@PathVariable Integer status, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            List<Order> orders = orderService.getOrdersByStatus(userId, status);
            return new ApiResponse<>(200, "获取成功", orders);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public ApiResponse<Void> cancelOrder(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.cancelOrder(orderNo, userId);
            return new ApiResponse<>(200, "取消成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderNo}")
    public ApiResponse<Void> payOrder(@PathVariable String orderNo, @RequestParam(value = "payType", required = false) Integer payType, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.payOrder(orderNo, userId, payType);
            return new ApiResponse<>(200, "支付成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/confirm/{orderNo}")
    public ApiResponse<Void> confirmReceipt(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.confirmReceipt(orderNo, userId);
            return new ApiResponse<>(200, "确认收货成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 应用折扣（优惠券、红包、金币抵现）
     */
    @PostMapping("/discount/apply")
    public ApiResponse<Order> applyDiscount(@RequestBody ApplyDiscountDTO dto, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            Order order = orderService.applyDiscount(dto, userId);
            return new ApiResponse<>(200, "折扣应用成功", order);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 订单发货
     */
    @PostMapping("/ship")
    public ApiResponse<Void> shipOrder(@RequestBody ShipOrderDTO shipOrderDTO, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.shipOrder(shipOrderDTO.getOrderId(), userId, shipOrderDTO.getLogisticsCompany(), shipOrderDTO.getLogisticsNo());
            return new ApiResponse<>(200, "发货成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 申请退款（未发货订单）
     */
    @PostMapping("/refund/apply/{orderNo}")
    public ApiResponse<Void> applyRefund(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.applyRefund(orderNo, userId);
            return new ApiResponse<>(200, "申请退款成功，订单进入申请中状态", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 商家同意退款（申请中 -> 已取消）
     */
    @PostMapping("/refund/approve/{orderNo}")
    public ApiResponse<Void> approveRefund(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.approveRefund(orderNo, userId);
            return new ApiResponse<>(200, "已同意退款，订单取消并已恢复库存", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 商家拒绝退款（申请中 -> 已支付）
     */
    @PostMapping("/refund/reject/{orderNo}")
    public ApiResponse<Void> rejectRefund(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            orderService.rejectRefund(orderNo, userId);
            return new ApiResponse<>(200, "已拒绝退款，订单回退为已支付", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }

    /**
     * 删除订单（仅允许已取消的订单）
     */
    @PostMapping("/delete/{orderNo}")
    public ApiResponse<Void> deleteOrder(@PathVariable String orderNo, HttpServletRequest request) {
      try {
        Long userId = SessionUserUtils.getUserId(request);
        orderService.deleteOrder(orderNo, userId);
        return new ApiResponse<>(200, "删除订单成功", null);
      } catch (Exception e) {
        return new ApiResponse<>(500, e.getMessage(), null);
      }
    }
    
    /**
     * 获取订单统计
     */
    @GetMapping("/statistics")
    public ApiResponse<OrderStatistics> getOrderStatistics(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            OrderStatistics statistics = new OrderStatistics();
            statistics.setTotalCount(orderService.getOrderCount(userId));
            statistics.setPendingPaymentCount(orderService.getOrderCountByStatus(userId, Order.OrderStatus.PENDING_PAYMENT));
            statistics.setPaidCount(orderService.getOrderCountByStatus(userId, Order.OrderStatus.PAID));
            statistics.setShippedCount(orderService.getOrderCountByStatus(userId, Order.OrderStatus.SHIPPED));
            statistics.setCompletedCount(orderService.getOrderCountByStatus(userId, Order.OrderStatus.COMPLETED));
            statistics.setCancelledCount(orderService.getOrderCountByStatus(userId, Order.OrderStatus.CANCELLED));
            return new ApiResponse<>(200, "获取成功", statistics);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 订单统计内部类
     */
    public static class OrderStatistics {
        private int totalCount;
        private int pendingPaymentCount;
        private int paidCount;
        private int shippedCount;
        private int completedCount;
        private int cancelledCount;
        
        // getter和setter方法
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getPendingPaymentCount() { return pendingPaymentCount; }
        public void setPendingPaymentCount(int pendingPaymentCount) { this.pendingPaymentCount = pendingPaymentCount; }
        
        public int getPaidCount() { return paidCount; }
        public void setPaidCount(int paidCount) { this.paidCount = paidCount; }
        
        public int getShippedCount() { return shippedCount; }
        public void setShippedCount(int shippedCount) { this.shippedCount = shippedCount; }
        
        public int getCompletedCount() { return completedCount; }
        public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
        
        public int getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(int cancelledCount) { this.cancelledCount = cancelledCount; }
    }
}
