package com.example.yddmall.service.impl;

import com.example.yddmall.entity.Merchant;
import com.example.yddmall.entity.User;
import com.example.yddmall.mapper.MerchantMapper;
import com.example.yddmall.mapper.UserMapper;
import com.example.yddmall.service.MerchantService;
import com.example.yddmall.utils.GenerateUniqueNoUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商家表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-08-29
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final MerchantMapper merchantMapper;

    @Autowired
    private HttpServletRequest request;

    private GenerateUniqueNoUtils generateUniqueNoUtils;

    @Autowired
    private UserMapper userMapper;

    public MerchantServiceImpl(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    @Override
    public Merchant add(Merchant merchant) {
        /* 1. 生成唯一商户编号 */
        String merchantNo = generateUniqueNoUtils.generateMerchantNo();
        merchant.setMerchantNo(merchantNo);
        
        /* 2. 设置商户状态 */
        merchant.setStatus((byte) 2);

        /* 3. 商户入库（主键回写） */
        merchantMapper.insert(merchant);
        Merchant merchantInDB = merchantMapper.selectById(merchant.getMerchantId());

        /* 4. 从当前请求的 Session 中获取 userId */
        Long userId = SessionUserUtils.getUserId(request);
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }

        /* 5. 验证用户存在性 */
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        /* 6. 关联商户与用户，升级角色 */
        user.setMerchantId(merchantInDB.getMerchantId());
        user.setRoleId(3L); //  3 是商户角色 ID
        userMapper.updateById(user);

        /* 7. 返回入库后的商户信息 */
        return merchantInDB;
    }

}
