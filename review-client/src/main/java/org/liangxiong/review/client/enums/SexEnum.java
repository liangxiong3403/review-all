package org.liangxiong.review.client.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-12-09 21:16
 * @description 通用枚举类, 自动映射数据库枚举值到字符串表示
 **/
@Getter
@AllArgsConstructor
public enum SexEnum {

    /**
     * 男性
     */
    MAN(1, "男"),
    /**
     * 女性
     */
    WOMAN(0, "女"),
    /**
     * 未知
     */
    UNKNOWN(-1, "未知");

    /**
     * 性别
     */
    @EnumValue
    private Integer sex;

    /**
     * 说明
     */
    @JsonValue
    private String description;

    @Override
    public String toString() {
        return this.description;
    }
}
