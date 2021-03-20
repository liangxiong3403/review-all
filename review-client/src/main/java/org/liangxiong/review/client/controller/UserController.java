package org.liangxiong.review.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.liangxiong.review.client.entity.User;
import org.liangxiong.review.client.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-19 21:37
 * @description
 **/
@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserMapper userMapper;

    @GetMapping("/list")
    public List<User> list() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lt("ID", 5);
        return userMapper.selectList(wrapper);
    }

    @PostMapping("add")
    public Boolean save(@RequestBody User user) {
        return userMapper.insert(user) == 1;
    }

    @GetMapping("/getOne")
    public User getOne(@RequestParam Long id) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", id);
        return userMapper.selectOne(wrapper);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam Long id) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", id);
        return userMapper.deleteByIdWithFill(User.builder().id(id).build()) == 1;
    }

    @DeleteMapping("/batchDelete")
    public Boolean batchDelete(@RequestBody List<Long> ids) {
        return userMapper.batchDeleteWithFill(User.builder().ids(ids).build()) > 0;
    }

    @PutMapping("/update")
    public Boolean delete(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", user.getId());
        return userMapper.update(user, wrapper) == 1;
    }
}
