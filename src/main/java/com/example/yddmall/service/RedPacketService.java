package com.example.yddmall.service;

import com.example.yddmall.entity.RedPacket;
import com.example.yddmall.entity.UserRedPacket;

import java.util.List;

public interface RedPacketService {
    List<RedPacket> listAvailable();
    /** 可领红包：可选按商家筛选 */
    List<RedPacket> listAvailable(Long merchantId);
    boolean claim(Long redPacketId, Long userId);
    List<UserRedPacket> listUserRedPackets(Long userId);
    List<UserRedPacket> listUserRedPackets(Long userId, Integer status, String orderNo);
    /** 商家或管理员发布红包 */
    RedPacket create(RedPacket redPacket);
    /** 按商户查询发布的红包 */
    List<RedPacket> listByMerchant(Long merchantId);
    /** 查询全部红包（管理员） */
    List<RedPacket> listAll();
    /** 根据ID获取红包 */
    RedPacket getById(Long id);
    /** 更新红包（字段校验在实现类中完成） */
    RedPacket update(RedPacket redPacket);
    /** 启停红包（status=1启用，0停用） */
    boolean setStatus(Long id, Integer status);
}