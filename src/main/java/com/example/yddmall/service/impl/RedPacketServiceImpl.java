package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.RedPacket;
import com.example.yddmall.entity.UserRedPacket;
import com.example.yddmall.mapper.RedPacketMapper;
import com.example.yddmall.mapper.UserRedPacketMapper;
import com.example.yddmall.service.RedPacketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RedPacketServiceImpl implements RedPacketService {

    @Resource
    private RedPacketMapper redPacketMapper;

    @Resource
    private UserRedPacketMapper userRedPacketMapper;

    @Override
    public List<RedPacket> listAvailable() {
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<RedPacket> qw = new QueryWrapper<>();
        qw.eq("status", 1)
          .gt("remaining_count", 0)
          .le("start_time", now)
          .ge("end_time", now);
        return redPacketMapper.selectList(qw);
    }

    @Override
    public List<RedPacket> listAvailable(Long merchantId) {
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<RedPacket> qw = new QueryWrapper<>();
        qw.eq("status", 1)
          .gt("remaining_count", 0)
          .le("start_time", now)
          .ge("end_time", now);
        if (merchantId != null) {
            qw.eq("merchant_id", merchantId);
        }
        return redPacketMapper.selectList(qw);
    }

    @Transactional
    @Override
    public boolean claim(Long redPacketId, Long userId) {
        RedPacket rp = redPacketMapper.selectById(redPacketId);
        if (rp == null || rp.getStatus() == null || rp.getStatus() != 1) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (rp.getStartTime() != null && now.isBefore(rp.getStartTime())) return false;
        if (rp.getEndTime() != null && now.isAfter(rp.getEndTime())) return false;
        if (rp.getRemainingCount() != null && rp.getRemainingCount() <= 0) return false;

        Integer limit = rp.getPerUserLimit();
        if (limit != null && limit > 0) {
            int claimed = userRedPacketMapper.countUserClaims(redPacketId, userId);
            if (claimed >= limit) return false;
        }

        rp.setRemainingCount(rp.getRemainingCount() - 1);
        redPacketMapper.updateById(rp);

        UserRedPacket urp = new UserRedPacket();
        urp.setRedPacketId(redPacketId);
        urp.setUserId(userId);
        urp.setStatus(0);
        urp.setClaimedAt(LocalDateTime.now());
        urp.setCreateTime(LocalDateTime.now());
        userRedPacketMapper.insert(urp);
        return true;
    }

    @Override
    public List<UserRedPacket> listUserRedPackets(Long userId) {
        QueryWrapper<UserRedPacket> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        return userRedPacketMapper.selectList(qw);
    }

    @Override
    public List<UserRedPacket> listUserRedPackets(Long userId, Integer status, String orderNo) {
        QueryWrapper<UserRedPacket> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        if (status != null) {
            qw.eq("status", status);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            qw.eq("order_no", orderNo);
        }
        return userRedPacketMapper.selectList(qw);
    }

    @Override
    @Transactional
    public RedPacket create(RedPacket redPacket) {
        if (redPacket == null) {
            throw new IllegalArgumentException("红包参数不能为空");
        }
        if (redPacket.getMerchantId() == null) {
            throw new IllegalArgumentException("merchantId不能为空");
        }
        if (redPacket.getTitle() == null || redPacket.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("红包标题不能为空");
        }
        java.math.BigDecimal amt = redPacket.getAmount();
        if (amt == null || amt.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("红包金额必须为正");
        }
        Integer total = redPacket.getTotalCount();
        if (total == null || total <= 0) {
            throw new IllegalArgumentException("总数量必须为正整数");
        }
        if (redPacket.getPerUserLimit() == null || redPacket.getPerUserLimit() < 0) {
            redPacket.setPerUserLimit(1);
        }
        if (redPacket.getStartTime() != null && redPacket.getEndTime() != null
                && redPacket.getEndTime().isBefore(redPacket.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        if (redPacket.getStatus() == null) {
            redPacket.setStatus(1);
        }
        redPacket.setRemainingCount(total);
        redPacketMapper.insert(redPacket);
        return redPacketMapper.selectById(redPacket.getId());
    }

    @Override
    public List<RedPacket> listByMerchant(Long merchantId) {
        QueryWrapper<RedPacket> qw = new QueryWrapper<>();
        qw.eq("merchant_id", merchantId);
        return redPacketMapper.selectList(qw);
    }

    @Override
    public List<RedPacket> listAll() {
        return redPacketMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public RedPacket getById(Long id) {
        return redPacketMapper.selectById(id);
    }

    @Override
    @Transactional
    public RedPacket update(RedPacket redPacket) {
        if (redPacket == null || redPacket.getId() == null) {
            throw new IllegalArgumentException("红包ID不能为空");
        }
        RedPacket db = redPacketMapper.selectById(redPacket.getId());
        if (db == null) {
            throw new IllegalArgumentException("红包不存在");
        }
        if (redPacket.getTitle() != null) db.setTitle(redPacket.getTitle());
        if (redPacket.getAmount() != null && redPacket.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            db.setAmount(redPacket.getAmount());
        }
        if (redPacket.getTotalCount() != null && redPacket.getTotalCount() > 0) {
            Integer oldTotal = db.getTotalCount() == null ? 0 : db.getTotalCount();
            Integer newTotal = redPacket.getTotalCount();
            db.setTotalCount(newTotal);
            Integer remain = db.getRemainingCount() == null ? oldTotal : db.getRemainingCount();
            int delta = newTotal - oldTotal;
            int newRemain = Math.max(0, Math.min(newTotal, remain + delta));
            db.setRemainingCount(newRemain);
        }
        if (redPacket.getPerUserLimit() != null && redPacket.getPerUserLimit() >= 1) {
            db.setPerUserLimit(redPacket.getPerUserLimit());
        }
        if (redPacket.getStartTime() != null) db.setStartTime(redPacket.getStartTime());
        if (redPacket.getEndTime() != null) {
            if (db.getStartTime() != null && redPacket.getEndTime().isBefore(db.getStartTime())) {
                throw new IllegalArgumentException("结束时间不能早于开始时间");
            }
            db.setEndTime(redPacket.getEndTime());
        }
        if (redPacket.getStatus() != null) db.setStatus(redPacket.getStatus());
        redPacketMapper.updateById(db);
        return redPacketMapper.selectById(db.getId());
    }

    @Override
    @Transactional
    public boolean setStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new IllegalArgumentException("参数无效");
        }
        RedPacket db = redPacketMapper.selectById(id);
        if (db == null) {
            throw new IllegalArgumentException("红包不存在");
        }
        db.setStatus(status);
        return redPacketMapper.updateById(db) > 0;
    }
}