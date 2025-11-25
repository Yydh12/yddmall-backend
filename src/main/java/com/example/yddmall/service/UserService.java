package com.example.yddmall.service;

import com.example.yddmall.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商城权限用户表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-07-31
 */
public interface UserService extends IService<User> {

    User login(User user);

    /**
     * 根据手机号登录
     */
    User loginByPhone(User user);

    User addUser(User user);

}
