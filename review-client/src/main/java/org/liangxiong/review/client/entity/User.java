package org.liangxiong.review.client.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-06 13:57
 * @description
 **/
@Builder
@Getter
@Setter
public class User {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;
}
