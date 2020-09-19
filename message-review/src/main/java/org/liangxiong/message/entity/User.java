package org.liangxiong.message.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-01-17 12:11
 * @description 用户
 **/
@Getter
@Setter
@ToString
public class User {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 性别
     */
    private String sex;
}
