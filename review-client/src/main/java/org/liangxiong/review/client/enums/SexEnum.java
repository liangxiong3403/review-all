package org.liangxiong.review.client.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-12-09 21:16
 * @description 通用枚举类, 自动映射数据库枚举值到字符串表示
 **/
public enum SexEnum implements IEnum<Integer> {

    MAN(1, "男"), WOMAN(0, "女");

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 说明
     */
    private String description;

    SexEnum(Integer sex, String description) {
        this.sex = sex;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return this.sex;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
