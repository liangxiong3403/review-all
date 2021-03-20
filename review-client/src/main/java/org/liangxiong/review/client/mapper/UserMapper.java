package org.liangxiong.review.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.liangxiong.review.client.entity.User;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-06 13:55
 * @description
 **/
public interface UserMapper extends BaseMapper<User> {

    Integer deleteByIdWithFill(User user);

    Integer batchDeleteWithFill(User user);
}
