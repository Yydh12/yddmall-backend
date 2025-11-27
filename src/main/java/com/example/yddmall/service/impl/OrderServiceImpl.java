package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yddmall.dto.CreateOrderDTO;
import com.example.yddmall.dto.DirectOrderDTO;
import com.example.yddmall.dto.ApplyDiscountDTO;
import com.example.yddmall.entity.*;
import com.example.yddmall.mapper.*;
import com.example.yddmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Autowired
    private ItemSkuMapper itemSkuMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private OrderStatusLogMapper orderStatusLogMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private RedPacketMapper redPacketMapper;

  @Autowired
  private UserRedPacketMapper userRedPacketMapper;

  @Autowired
  private ItemMapper itemMapper;

    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;

    @Autowired
    private com.example.yddmall.service.FreightService freightService;
    
    @Override
    @Transactional
    public Order createOrder(CreateOrderDTO createOrderDTO, Long userId) {
        // 验证收货地址
        Address address = addressMapper.selectById(createOrderDTO.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在或无权限");
        }
        
        // 根据购物车项创建订单项（简化版本）
        List<OrderItem> orderItems = createOrderItemsFromCart(createOrderDTO.getCartItemIds(), userId);
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("购物车为空或商品不属于当前用户");
        }
        
        // 验证商品库存
        for (OrderItem item : orderItems) {
            ItemSku sku = itemSkuMapper.selectById(item.getSkuId());
            if (sku == null || sku.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }
        }
        
        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setTotalAmount(itemTotal);
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(createOrderDTO.getAddressId());
        order.setTotalAmount(totalAmount);
        // 基于地址经纬度计算运费
        BigDecimal freight = freightService.computeFreight(address);
        order.setFreightAmount(freight);
        order.setPayAmount(totalAmount.add(freight));
        order.setDiscountAmount(BigDecimal.ZERO);
        // 允许创建时未选择支付方式，设置默认值为微信
        order.setPayType(createOrderDTO.getPayType() != null ? createOrderDTO.getPayType() : Order.PayType.WECHAT);
        order.setOrderStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(formatAddress(address));
        order.setBuyerMessage(createOrderDTO.getBuyerMessage());
        
        // 保存订单
        this.save(order);
        
        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getOrderId());
            item.setOrderNo(order.getOrderNo());
        }
        if (orderItems != null && !orderItems.isEmpty()) {
            orderItemMapper.batchInsert(orderItems);
        }
        
        // 扣减库存
        for (OrderItem item : orderItems) {
            ItemSku sku = itemSkuMapper.selectById(item.getSkuId());
            sku.setQuantity(sku.getQuantity() - item.getQuantity());
            itemSkuMapper.updateById(sku);
        }
        
        return order;
    }
    
    /**
     * 根据购物车项创建订单项
     */
    private List<OrderItem> createOrderItemsFromCart(List<Long> cartItemIds, Long userId) {
        List<OrderItem> orderItems = new ArrayList<>();
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return orderItems;
        }

        // 通过主键批量查询购物车记录（避免使用已弃用的 selectBatchIds）
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>().in(Cart::getCartId, cartItemIds));
        if (carts == null || carts.isEmpty()) {
            return orderItems;
        }

        for (Cart cart : carts) {
            // 仅处理当前用户的购物车项
            if (cart == null || !userId.equals(cart.getUserId())) {
                continue;
            }
            OrderItem item = new OrderItem();
            item.setItemId(cart.getItemId());
            item.setSkuId(cart.getSkuId());
            item.setItemName(cart.getProductName());
            item.setSkuName(cart.getSkuName());
            item.setItemPic(cart.getProductImage());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            // 计算小计金额
            if (cart.getPrice() != null && cart.getQuantity() != null) {
                item.setTotalAmount(cart.getPrice().multiply(new java.math.BigDecimal(cart.getQuantity())));
            }
            orderItems.add(item);
        }

        return orderItems;
    }
    
    @Override
    @Transactional
    public Order createDirectOrder(DirectOrderDTO directOrderDTO, Long userId) {
        // 验证收货地址
        Address address = addressMapper.selectById(directOrderDTO.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("收货地址不存在或无权限");
        }
        
        // 创建订单项
        List<OrderItem> orderItems = createOrderItemsFromDirect(directOrderDTO.getOrderItems());
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("商品信息为空，无法创建订单");
        }
        
        // 验证商品库存
        for (OrderItem item : orderItems) {
            ItemSku sku = itemSkuMapper.selectById(item.getSkuId());
            if (sku == null || sku.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }
        }
        
        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setTotalAmount(itemTotal);
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(directOrderDTO.getAddressId());
        order.setTotalAmount(totalAmount);
        // 基于地址经纬度计算运费
        BigDecimal freight = freightService.computeFreight(address);
        order.setFreightAmount(freight);
        order.setPayAmount(totalAmount.add(freight));
        order.setDiscountAmount(BigDecimal.ZERO);
        // 允许创建时未选择支付方式，设置默认值为微信
        order.setPayType(directOrderDTO.getPayType() != null ? directOrderDTO.getPayType() : Order.PayType.WECHAT);
        order.setOrderStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(formatAddress(address));
        order.setBuyerMessage(directOrderDTO.getBuyerMessage());
        
        // 保存订单
        this.save(order);
        
        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getOrderId());
            item.setOrderNo(order.getOrderNo());
        }
        if (orderItems != null && !orderItems.isEmpty()) {
            orderItemMapper.batchInsert(orderItems);
        }
        
        // 扣减库存
        for (OrderItem item : orderItems) {
            ItemSku sku = itemSkuMapper.selectById(item.getSkuId());
            sku.setQuantity(sku.getQuantity() - item.getQuantity());
            itemSkuMapper.updateById(sku);
        }
        
        return order;
    }
    
    /**
     * 根据直接购买的商品信息创建订单项
     */
    private List<OrderItem> createOrderItemsFromDirect(List<DirectOrderDTO.OrderItemDTO> orderItemDTOs) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (DirectOrderDTO.OrderItemDTO itemDTO : orderItemDTOs) {
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(itemDTO.getItemId());
            orderItem.setSkuId(itemDTO.getSkuId());
            orderItem.setItemName(itemDTO.getProductName());
            orderItem.setSkuName(itemDTO.getSkuName());
            orderItem.setPrice(itemDTO.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setItemPic(itemDTO.getProductImage());
            orderItem.setTotalAmount(itemDTO.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }

    @Override
    @Transactional
    public Order applyDiscount(ApplyDiscountDTO dto, Long userId) {
        // 获取订单
        Order order = getOrderDetail(dto.getOrderNo(), userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("仅待支付订单可应用折扣");
        }

        BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal freight = order.getFreightAmount() != null ? order.getFreightAmount() : BigDecimal.ZERO;
        BigDecimal discountSum = BigDecimal.ZERO;

        // 优惠券
        UserCoupon usedUserCoupon = null;
        if (dto.getCouponId() != null) {
            UserCoupon uc = userCouponMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCoupon>()
                    .eq(UserCoupon::getCouponId, dto.getCouponId())
                    .eq(UserCoupon::getUserId, userId)
                    .eq(UserCoupon::getStatus, 0)
                    .last("LIMIT 1"));
            if (uc != null) {
                Coupon coupon = couponMapper.selectById(dto.getCouponId());
                if (coupon != null && coupon.getStatus() != null && coupon.getStatus() == 1) {
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    boolean timeOk = (coupon.getStartTime() == null || !now.isBefore(coupon.getStartTime()))
                            && (coupon.getEndTime() == null || !now.isAfter(coupon.getEndTime()));
                    boolean minSpendOk = coupon.getMinSpend() == null || totalAmount.compareTo(coupon.getMinSpend()) >= 0;
                    if (timeOk && minSpendOk) {
                        BigDecimal couponDiscount = BigDecimal.ZERO;
                        Integer type = coupon.getDiscountType();
                        if (type != null && type == 2) {
                            // 百分比折扣，discountValue 代表百分比，如 10 表示 10%
                            BigDecimal percent = coupon.getDiscountValue() != null ? coupon.getDiscountValue() : BigDecimal.ZERO;
                            couponDiscount = totalAmount.multiply(percent).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                        } else {
                            // 固定金额折扣
                            couponDiscount = coupon.getDiscountValue() != null ? coupon.getDiscountValue() : BigDecimal.ZERO;
                        }
                        if (couponDiscount.compareTo(totalAmount) > 0) {
                            couponDiscount = totalAmount;
                        }
                        discountSum = discountSum.add(couponDiscount);
                        usedUserCoupon = uc;
                    }
                }
            }
        }

        // 红包
        UserRedPacket usedUserRedPacket = null;
        if (dto.getRedPacketId() != null) {
            UserRedPacket urp = userRedPacketMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRedPacket>()
                    .eq(UserRedPacket::getRedPacketId, dto.getRedPacketId())
                    .eq(UserRedPacket::getUserId, userId)
                    .eq(UserRedPacket::getStatus, 0)
                    .last("LIMIT 1"));
            if (urp != null) {
                RedPacket rp = redPacketMapper.selectById(dto.getRedPacketId());
                if (rp != null && rp.getStatus() != null && rp.getStatus() == 1) {
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    boolean timeOk = (rp.getStartTime() == null || !now.isBefore(rp.getStartTime()))
                            && (rp.getEndTime() == null || !now.isAfter(rp.getEndTime()));
                    if (timeOk) {
                        // 仅允许抵扣该商家的商品小计
                        BigDecimal merchantSubtotal = BigDecimal.ZERO;
                        if (rp.getMerchantId() != null) {
                            List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
                            if (orderItems != null) {
                                for (OrderItem oi : orderItems) {
                                    if (oi == null || oi.getItemId() == null) continue;
                                    Item itemEntity = itemMapper.selectById(oi.getItemId());
                                    if (itemEntity != null && itemEntity.getSellerId() != null
                                            && itemEntity.getSellerId().equals(rp.getMerchantId())) {
                                        BigDecimal itemTotal = oi.getTotalAmount() != null ? oi.getTotalAmount() :
                                                (oi.getPrice() != null && oi.getQuantity() != null
                                                        ? oi.getPrice().multiply(new BigDecimal(oi.getQuantity()))
                                                        : BigDecimal.ZERO);
                                        merchantSubtotal = merchantSubtotal.add(itemTotal);
                                    }
                                }
                            }
                        } else {
                            // 理论上商家红包必有 merchantId；兜底为订单总额
                            merchantSubtotal = totalAmount;
                        }

                        BigDecimal rpAmount = rp.getAmount() != null ? rp.getAmount() : BigDecimal.ZERO;
                        // 红包抵扣额不得超过商家商品小计
                        if (rpAmount.compareTo(merchantSubtotal) > 0) {
                            rpAmount = merchantSubtotal;
                        }
                        // 再次兜底不得超过订单商品总额
                        if (rpAmount.compareTo(totalAmount) > 0) {
                            rpAmount = totalAmount;
                        }
                        if (merchantSubtotal.compareTo(BigDecimal.ZERO) > 0 && rpAmount.compareTo(BigDecimal.ZERO) > 0) {
                            discountSum = discountSum.add(rpAmount);
                            usedUserRedPacket = urp;
                        }
                    }
                }
            }
        }

        // 金币抵现：1金币=0.01元，最多抵扣商品金额的30%
        long coinsToDeduct = 0L;
        if (dto.getCoinAmount() != null && dto.getCoinAmount() > 0) {
            UserCoinWallet wallet = userCoinWalletMapper.findByUserId(userId);
            if (wallet != null && wallet.getBalance() != null && wallet.getBalance() > 0) {
                long requested = dto.getCoinAmount();
                long affordable = wallet.getBalance();
                // 商品金额的30%对应的最多金币
                long maxCoinsByCap = totalAmount.multiply(new BigDecimal("0.3"))
                        .divide(new BigDecimal("0.01"), 0, java.math.RoundingMode.DOWN).longValue();
                coinsToDeduct = Math.min(requested, Math.min(affordable, Math.max(0L, maxCoinsByCap)));
                if (coinsToDeduct > 0) {
                    BigDecimal coinDiscount = new BigDecimal(coinsToDeduct).multiply(new BigDecimal("0.01"));
                    discountSum = discountSum.add(coinDiscount);
                }
            }
        }

        // 折扣不得超过商品金额
        if (discountSum.compareTo(totalAmount) > 0) {
            discountSum = totalAmount;
        }

        // 更新订单金额
        order.setDiscountAmount(discountSum);
        BigDecimal payAmount = totalAmount.add(freight).subtract(discountSum);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }
        order.setPayAmount(payAmount);
        this.updateById(order);

        // 标记优惠券/红包为已使用
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (usedUserCoupon != null) {
            usedUserCoupon.setStatus(1);
            usedUserCoupon.setUsedAt(now);
            usedUserCoupon.setOrderNo(order.getOrderNo());
            userCouponMapper.updateById(usedUserCoupon);
        }
        if (usedUserRedPacket != null) {
            usedUserRedPacket.setStatus(1);
            usedUserRedPacket.setUsedAt(now);
            usedUserRedPacket.setOrderNo(order.getOrderNo());
            userRedPacketMapper.updateById(usedUserRedPacket);
        }

        // 扣减金币
        if (coinsToDeduct > 0) {
            UserCoinWallet wallet = userCoinWalletMapper.findByUserId(userId);
            if (wallet != null) {
                wallet.setBalance(wallet.getBalance() - coinsToDeduct);
                wallet.setUpdateTime(now);
                userCoinWalletMapper.updateById(wallet);
            }
        }

        return order;
    }
    
    @Override
    public Order getOrderDetail(String orderNo, Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId);
        return this.getOne(wrapper);
    }
    
    @Override
    public Order getOrderWithItems(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order != null) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
            order.setOrderItems(orderItems);
        }
        return order;
    }

    @Override
    public com.example.yddmall.vo.DiscountDetailVO getOrderDiscountDetail(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            return null;
        }

        java.math.BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal freight = order.getFreightAmount() != null ? order.getFreightAmount() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal discountSum = order.getDiscountAmount() != null ? order.getDiscountAmount() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal payAmount = order.getPayAmount() != null ? order.getPayAmount() : totalAmount.add(freight).subtract(discountSum);

        // 查询已使用的优惠券和红包记录
        UserCoupon usedCoupon = userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderNo, orderNo)
                .eq(UserCoupon::getStatus, 1)
                .last("LIMIT 1"));

        UserRedPacket usedRedPacket = userRedPacketMapper.selectOne(new LambdaQueryWrapper<UserRedPacket>()
                .eq(UserRedPacket::getUserId, userId)
                .eq(UserRedPacket::getOrderNo, orderNo)
                .eq(UserRedPacket::getStatus, 1)
                .last("LIMIT 1"));

        java.math.BigDecimal couponDiscount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal redPacketDiscount = java.math.BigDecimal.ZERO;

        Coupon coupon = null;
        if (usedCoupon != null && usedCoupon.getCouponId() != null) {
            coupon = couponMapper.selectById(usedCoupon.getCouponId());
            if (coupon != null) {
                Integer type = coupon.getDiscountType();
                java.math.BigDecimal dv = coupon.getDiscountValue() != null ? coupon.getDiscountValue() : java.math.BigDecimal.ZERO;
                if (type != null && type == 2) {
                    // 百分比折扣
                    couponDiscount = totalAmount.multiply(dv).divide(new java.math.BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                } else {
                    // 固定金额折扣
                    couponDiscount = dv;
                }
                if (couponDiscount.compareTo(totalAmount) > 0) {
                    couponDiscount = totalAmount;
                }
            }
        }

        RedPacket redPacket = null;
        if (usedRedPacket != null && usedRedPacket.getRedPacketId() != null) {
            redPacket = redPacketMapper.selectById(usedRedPacket.getRedPacketId());
            if (redPacket != null) {
                redPacketDiscount = redPacket.getAmount() != null ? redPacket.getAmount() : java.math.BigDecimal.ZERO;
                if (redPacketDiscount.compareTo(totalAmount) > 0) {
                    redPacketDiscount = totalAmount;
                }
            }
        }

        java.math.BigDecimal coinDiscount = discountSum.subtract(couponDiscount).subtract(redPacketDiscount);
        if (coinDiscount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            coinDiscount = java.math.BigDecimal.ZERO;
        }
        Long coinUsed = coinDiscount.divide(new java.math.BigDecimal("0.01"), 0, java.math.RoundingMode.DOWN).longValue();

        com.example.yddmall.vo.DiscountDetailVO vo = new com.example.yddmall.vo.DiscountDetailVO();
        vo.setOrderNo(orderNo);
        vo.setTotalAmount(totalAmount);
        vo.setFreightAmount(freight);
        vo.setDiscountAmount(discountSum);
        vo.setPayAmount(payAmount);
        vo.setCoupon(coupon);
        vo.setRedPacket(redPacket);
        vo.setCouponDiscount(couponDiscount);
        vo.setRedPacketDiscount(redPacketDiscount);
        vo.setCoinDiscount(coinDiscount);
        vo.setCoinUsed(coinUsed);
        return vo;
    }
    
    @Override
    public IPage<Order> getUserOrders(Page<Order> page, Long userId, Integer orderStatus) {
        IPage<Order> orderPage = orderMapper.selectPageByUserIdAndStatus(page, userId, orderStatus);
        // 填充订单项以便前端一次性展示商品信息
        if (orderPage != null && orderPage.getRecords() != null) {
            for (Order order : orderPage.getRecords()) {
                List<OrderItem> items = orderItemMapper.selectByOrderNo(order.getOrderNo());
                order.setOrderItems(items);
            }
        }
        return orderPage;
    }
    
    @Override
    public List<Order> getOrdersByStatus(Long userId, Integer orderStatus) {
        return orderMapper.selectByUserIdAndStatus(userId, orderStatus);
    }
    
    @Override
    @Transactional
    public void cancelOrder(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getOrderStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("只有待支付的订单才能取消");
        }
        
        // 恢复库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
        for (OrderItem item : orderItems) {
            ItemSku sku = itemSkuMapper.selectById(item.getSkuId());
            sku.setQuantity(sku.getQuantity() + item.getQuantity());
            itemSkuMapper.updateById(sku);
        }
        
        // 置为已取消，不删除订单
        int fromStatus = order.getOrderStatus();
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        String remark = order.getRemark();
        String change = String.format("[取消订单] from:%d to:%d", fromStatus, Order.OrderStatus.CANCELLED);
        order.setRemark(remark == null || remark.isEmpty() ? change : (remark + " | " + change));
        this.updateById(order);
    }
    
    @Override
    @Transactional
    public void payOrder(String orderNo, Long userId, Integer payType) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getOrderStatus() == Order.OrderStatus.PAID) {
            return; // 如果订单已支付，直接返回成功
        }

        if (order.getOrderStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        
        // 如果支付时携带支付方式，更新订单支付方式；否则沿用现有或默认
        if (payType != null) {
            order.setPayType(payType);
        }
        order.setOrderStatus(Order.OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);
    }
    
    @Override
    @Transactional
    public void confirmReceipt(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getOrderStatus() != Order.OrderStatus.SHIPPED) {
            throw new RuntimeException("只有已发货的订单才能确认收货");
        }
        
        order.setOrderStatus(Order.OrderStatus.COMPLETED);
        order.setReceiveTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId, Long userId, String logisticsCompany, String logisticsNo) {
        Order order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在或无权限");
        }

        if (order.getOrderStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("只有已支付的订单才能发货");
        }

        order.setOrderStatus(Order.OrderStatus.SHIPPED);
        order.setDeliveryTime(LocalDateTime.now());

        String remark = order.getRemark();
        String shipInfo = String.format("[发货] 公司:%s 单号:%s", logisticsCompany, logisticsNo);
        order.setRemark(remark == null || remark.isEmpty() ? shipInfo : (remark + " | " + shipInfo));

        this.updateById(order);
    }

    @Override
    @Transactional
    public void applyRefund(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        // 仅允许已支付且未发货的订单申请退款
        if (order.getOrderStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("仅未发货的已支付订单可申请退款");
        }
        // 将订单置为申请中（由 MyBatis 自动填充 update_time）
        int fromStatus = order.getOrderStatus();
        order.setOrderStatus(Order.OrderStatus.REFUND_APPLIED);
        String remark = order.getRemark();
        String change = String.format("[申请退款] from:%d to:%d", fromStatus, Order.OrderStatus.REFUND_APPLIED);
        order.setRemark(remark == null || remark.isEmpty() ? change : (remark + " | " + change));
        this.updateById(order);
    }

    /**
     * 商家同意退款：恢复库存并取消订单
     */
    @Override
    @Transactional
    public void approveRefund(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != Order.OrderStatus.REFUND_APPLIED) {
            throw new RuntimeException("仅申请中的订单可同意退款");
        }
        // 恢复库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
        for (OrderItem item : orderItems) {
            ItemSku sku = itemSkuMapper.selectById(item.getSkuId());
            sku.setQuantity(sku.getQuantity() + item.getQuantity());
            itemSkuMapper.updateById(sku);
        }

        // 置为已取消，不删除订单
        int fromStatus = order.getOrderStatus();
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        String remark = order.getRemark();
        String change = String.format("[同意退款] from:%d to:%d", fromStatus, Order.OrderStatus.CANCELLED);
        order.setRemark(remark == null || remark.isEmpty() ? change : (remark + " | " + change));
        this.updateById(order);
    }

    /**
     * 商家拒绝退款：回退为已支付
     */
    @Override
    @Transactional
    public void rejectRefund(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != Order.OrderStatus.REFUND_APPLIED) {
            throw new RuntimeException("仅申请中的订单可拒绝退款");
        }
        int fromStatus = order.getOrderStatus();
        order.setOrderStatus(Order.OrderStatus.PAID);
        String remark = order.getRemark();
        String change = String.format("[拒绝退款] from:%d to:%d", fromStatus, Order.OrderStatus.PAID);
        order.setRemark(remark == null || remark.isEmpty() ? change : (remark + " | " + change));
        this.updateById(order);
    }
    
    @Override
    public int getOrderCount(Long userId) {
        return orderMapper.countByUserId(userId);
    }
    
    @Override
    public int getOrderCountByStatus(Long userId, Integer orderStatus) {
        return orderMapper.countByUserIdAndStatus(userId, orderStatus);
    }
    
    /**
     * 删除订单（仅允许已取消的订单进行硬删除）
     */
    @Override
    @Transactional
    public void deleteOrder(String orderNo, Long userId) {
        Order order = getOrderDetail(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("仅已取消的订单可删除");
        }
        // 级联删除与订单关联的数据
        orderItemMapper.deleteByOrderNo(orderNo);
        paymentRecordMapper.deleteByOrderNo(orderNo);
        orderStatusLogMapper.deleteByOrderNo(orderNo);
        orderMapper.deleteByOrderNo(orderNo);
    }
    
    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    /**
     * 格式化地址
     */
    private String formatAddress(Address address) {
        return address.getProvince() + " " + address.getCity() + " " + 
               address.getDistrict() + " " + address.getDetailAddress();
    }
}
