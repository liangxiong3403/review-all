package org.liangxiong.review.client.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.liangxiong.common.converter.LocalDateTimeConverter;

import java.time.LocalDateTime;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-27 08:03
 * @description
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    @Excel(name = "id", orderNum = "1", width = 20)
    @ExcelProperty(value = "id", index = 0)
    private Long id;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @ExcelIgnore
    @com.alibaba.excel.annotation.ExcelIgnore
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建时间", orderNum = "2", width = 20, format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "创建时间", index = 1, converter = LocalDateTimeConverter.class)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT)
    @ExcelIgnore
    @com.alibaba.excel.annotation.ExcelIgnore
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "修改时间", orderNum = "3", width = 20, format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "修改时间", index = 2, converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedTime;

    @TableField(fill = FieldFill.UPDATE)
    @ExcelIgnore
    @com.alibaba.excel.annotation.ExcelIgnore
    private String updatedBy;
}
