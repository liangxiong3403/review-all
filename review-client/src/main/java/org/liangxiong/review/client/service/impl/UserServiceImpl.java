package org.liangxiong.review.client.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.liangxiong.review.client.entity.User;
import org.liangxiong.review.client.mapper.UserMapper;
import org.liangxiong.review.client.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-27 08:00
 * @description
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Integer deleteByIdWithFill(User user) {
        return userMapper.deleteByIdWithFill(user);
    }

    @Override
    public Integer batchDeleteWithFill(User user) {
        return userMapper.batchDeleteWithFill(user);
    }

}
