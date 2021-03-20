package org.liangxiong.review.client.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.liangxiong.review.client.enums.SexEnum;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 逻辑删除字段
     */
    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updatedBy;

    /**
     * 数据库最好使用int类型;虽然高版本mybatis-plus可以识别数据库tinyint类型的枚举
     */
    private SexEnum sex;

    /**
     * 租户id
     */
    private Long tenantId;

    @TableField(exist = false)
    private List<Long> ids;
}
