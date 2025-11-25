package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.User;
import com.example.yddmall.mapper.UserMapper;
import com.example.yddmall.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yddmall.utils.GenerateUniqueNoUtils;
import com.example.yddmall.utils.PasswordUtils;
import org.springframework.stereotype.Service;
import static org.apache.logging.log4j.util.Strings.trimToNull;

/**
 * <p>
 * 商城权限用户表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-07-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private GenerateUniqueNoUtils generateUniqueNoUtils;

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User login(User user) {
        // 1. 根据用户名一次性查出唯一用户
        User dbUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("username", user.getUsername())
        );
        if (dbUser == null) return null;

        // 2. 校验密码
        return PasswordUtils.verifyPassword(
                user.getPassword(),
                dbUser.getPassword(),
                dbUser.getSalt())
                ? dbUser
                : null;
    }

    @Override
    public User loginByPhone(User user) {
        // 1. 根据手机号查唯一用户（忽略空字符串）
        String phone = trimToNull(user.getPhone());
        if (phone == null) return null;
        User dbUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("phone", phone)
        );
        if (dbUser == null) return null;

        // 2. 校验密码
        return PasswordUtils.verifyPassword(
                user.getPassword(),
                dbUser.getPassword(),
                dbUser.getSalt())
                ? dbUser
                : null;
    }

    @Override
    public User addUser(User user) {
        try {
            // 生成唯一用户名（不依赖外部工具，确保不为null）
            String username = generateUniqueUsername();
            user.setUsername(username);
            String[] result = PasswordUtils.encryptPassword(user.getPassword());
            user.setPassword(result[0]);
            user.setSalt(result[1]);
            // 邮箱空字符串转null
            user.setEmail(trimToNull(user.getEmail()));
            // 手机号空字符串转null（保留有效手机号）
            String phone = trimToNull(user.getPhone());
            user.setPhone(phone);
            user.setStatus((byte) 1);
            // 默认新用户角色：普通用户（roleId = 4）
            user.setRoleId(4L);
            // 先入库，拿到自增的 userId
            userMapper.insert(user);
            // 基于自增ID生成用户编号：U + 9位零填充
            if (user.getUserId() != null) {
                String userNo = "U" + String.format("%09d", user.getUserId());
                user.setUserNo(userNo);
                // 回写用户编号
                userMapper.updateById(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    /**
     * 生成唯一的 ydd 前缀用户名，尽量避免冲突
     */
    private String generateUniqueUsername() {
        final String prefix = "ydd";
        // 先随机尝试若干次
        for (int i = 0; i < 8; i++) {
            String candidate = prefix + String.format("%05d", (int) ((Math.random() * 90000) + 10000));
            Long cnt = userMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", candidate));
            if (cnt == null || cnt == 0L) {
                return candidate;
            }
        }
        // 退化方案：基于最大 ydd 数字+1
        try {
            String max = userMapper.selectMaxYddUsername();
            if (max != null && max.startsWith(prefix)) {
                String tail = max.substring(prefix.length());
                long num = Long.parseLong(tail) + 1;
                // 保持5位数字部分的零填充显示
                return prefix + String.format("%05d", num);
            }
        } catch (Exception ignored) {}
        // 最后兜底：时间戳
        return prefix + System.currentTimeMillis();
    }
}
