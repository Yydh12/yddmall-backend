package com.example.yddmall.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.yddmall.entity.Item;
import com.example.yddmall.entity.Merchant;
import com.example.yddmall.entity.User;
import com.example.yddmall.mapper.ItemMapper;
import com.example.yddmall.mapper.MerchantMapper;
import com.example.yddmall.mapper.UserMapper;

public class GenerateUniqueNoUtils {

    @Autowired
    private static UserMapper userMapper;
    
    @Autowired
    private static MerchantMapper merchantMapper;
    
    @Autowired
    private static ItemMapper itemMapper;
    
    // 编号前缀映射
    private static final Map<String, String> NO_PREFIX_MAP = new HashMap<>();
    private static final Map<String, Function<LambdaQueryWrapper<?>, Long>> COUNT_FUNCTION_MAP = new HashMap<>();
    
    static {
        // 初始化编号前缀
        NO_PREFIX_MAP.put("user", "U");
        NO_PREFIX_MAP.put("merchant", "M");
        NO_PREFIX_MAP.put("item", "I");
        
        // 初始化计数函数
        COUNT_FUNCTION_MAP.put("user", wrapper -> {
            LambdaQueryWrapper<User> userWrapper = (LambdaQueryWrapper<User>) wrapper;
            return userMapper.selectCount(userWrapper);
        });
        COUNT_FUNCTION_MAP.put("merchant", wrapper -> {
            LambdaQueryWrapper<Merchant> merchantWrapper = (LambdaQueryWrapper<Merchant>) wrapper;
            return merchantMapper.selectCount(merchantWrapper);
        });
        COUNT_FUNCTION_MAP.put("item", wrapper -> {
            LambdaQueryWrapper<Item> itemWrapper = (LambdaQueryWrapper<Item>) wrapper;
            return itemMapper.selectCount(itemWrapper);
        });
    }
    


    /**
     * 生成唯一编号（通用方法）
     * @param entityType 实体类型：user, merchant, item
     * @return 唯一编号
     */
    public String generateUniqueNo(String entityType) {
        validateEntityType(entityType);
        
        String prefix = NO_PREFIX_MAP.get(entityType.toLowerCase());
        String uniqueNo;
        int maxRetry = 5;
        int retryCount = 0;
        
        do {
            // 生成随机编号
            uniqueNo = prefix + String.format("%08d", 
                (int)((Math.random() * 90000000) + 10000000)
            );
            
            // 检查是否已存在
            if (!isNoExists(entityType, uniqueNo)) {
                break; // 编号唯一，退出循环
            }
            
            retryCount++;
            if (retryCount >= maxRetry) {
                throw new RuntimeException("生成" + getEntityName(entityType) + "编号失败，请重试");
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("生成" + getEntityName(entityType) + "编号被中断");
            }
            
        } while (true);
        
        return uniqueNo;
    }
    
    /**
     * 检查编号是否存在
     */
    private boolean isNoExists(String entityType, String no) {
        LambdaQueryWrapper<?> queryWrapper = createQueryWrapper(entityType, no);
        Function<LambdaQueryWrapper<?>, Long> countFunction = COUNT_FUNCTION_MAP.get(entityType.toLowerCase());
        
        if (countFunction == null) {
            throw new IllegalArgumentException("不支持的实体类型: " + entityType);
        }
        
        Long count = countFunction.apply(queryWrapper);
        return count > 0;
    }
    
    /**
     * 创建查询包装器
     */
    private LambdaQueryWrapper<?> createQueryWrapper(String entityType, String no) {
        switch (entityType.toLowerCase()) {
            case "user":
                LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(User::getUserNo, no);
                return userWrapper;
            case "merchant":
                LambdaQueryWrapper<Merchant> merchantWrapper = new LambdaQueryWrapper<>();
                merchantWrapper.eq(Merchant::getMerchantNo, no);
                return merchantWrapper;
            case "item":
                LambdaQueryWrapper<Item> itemWrapper = new LambdaQueryWrapper<>();
                itemWrapper.eq(Item::getItemNo, no);
                return itemWrapper;
            default:
                throw new IllegalArgumentException("不支持的实体类型: " + entityType);
        }
    }
    
    /**
     * 验证实体类型
     */
    private void validateEntityType(String entityType) {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("实体类型不能为空");
        }
        
        if (!NO_PREFIX_MAP.containsKey(entityType.toLowerCase())) {
            throw new IllegalArgumentException("不支持的实体类型: " + entityType + 
                "，支持的类型: " + NO_PREFIX_MAP.keySet());
        }
    }
    
    /**
     * 获取实体中文名称
     */
    private String getEntityName(String entityType) {
        switch (entityType.toLowerCase()) {
            case "user": return "用户";
            case "merchant": return "商户";
            case "item": return "商品";
            default: return entityType;
        }
    }
    
    /**
     * 便捷方法：生成用户编号
     */
    public String generateUserNo() {
        return generateUniqueNo("user");
    }
    
    /**
     * 便捷方法：生成商户编号
     */
    public String generateMerchantNo() {
        return generateUniqueNo("merchant");
    }
    
    /**
     * 便捷方法：生成商品编号
     */
    public String generateItemNo() {
        return generateUniqueNo("item");
    }
}
