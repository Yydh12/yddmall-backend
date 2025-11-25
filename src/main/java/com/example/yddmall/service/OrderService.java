package com.example.yddmall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yddmall.dto.CreateOrderDTO;
import com.example.yddmall.dto.DirectOrderDTO;
import com.example.yddmall.dto.ApplyDiscountDTO;
import com.example.yddmall.entity.Order;

import java.util.List;

public interface OrderService {
    
    /**
     * 创建订单（通过购物车）
     */
    Order createOrder(CreateOrderDTO createOrderDTO, Long userId);
    
    /**
     * 创建订单（直接购买，不经过购物车）
     */
    Order createDirectOrder(DirectOrderDTO directOrderDTO, Long userId);
    
    /**
     * 获取订单详情
     */
    Order getOrderDetail(String orderNo, Long userId);
    
    /**
     * 获取订单详情（包含订单项）
     */
    Order getOrderWithItems(String orderNo, Long userId);
    
    /**
     * 分页获取用户的订单列表（可选状态过滤，返回包含订单项）
     */
    IPage<Order> getUserOrders(Page<Order> page, Long userId, Integer orderStatus);
    
    /**
     * 根据订单状态获取订单列表
     */
    List<Order> getOrdersByStatus(Long userId, Integer orderStatus);
    
    /**
     * 取消订单
     */
    void cancelOrder(String orderNo, Long userId);
    
    /**
     * 支付订单（可携带支付方式）
     */
    void payOrder(String orderNo, Long userId, Integer payType);
    
    /**
     * 确认收货
     */
    void confirmReceipt(String orderNo, Long userId);
    
    /**
     * 获取订单统计信息
     */
    int getOrderCount(Long userId);
    
    /**
     * 获取不同状态的订单数量
     */
    int getOrderCountByStatus(Long userId, Integer orderStatus);

    /**
     * 订单发货
     */
    void shipOrder(Long orderId, Long userId, String logisticsCompany, String logisticsNo);

    /**
     * 申请退款（未发货，用户发起）。改为两步：先置为申请中。
     */
    void applyRefund(String orderNo, Long userId);

    /**
     * 商家同意退款（申请中 -> 已取消），恢复库存
     */
    void approveRefund(String orderNo, Long userId);

    /**
     * 商家拒绝退款（申请中 -> 已支付）
     */
    void rejectRefund(String orderNo, Long userId);

    /**
     * 删除订单（仅允许已取消的订单进行硬删除）
     */
    void deleteOrder(String orderNo, Long userId);

    /**
     * 应用折扣（优惠券、红包、金币抵现）
     */
    Order applyDiscount(ApplyDiscountDTO dto, Long userId);

    /**
     * 获取订单的优惠详情（优惠券/红包/金币抵扣的明细）
     */
    com.example.yddmall.vo.DiscountDetailVO getOrderDiscountDetail(String orderNo, Long userId);
}