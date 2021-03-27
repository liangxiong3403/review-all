package org.liangxiong.review.client.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.liangxiong.review.client.enums.SexEnum;

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
@Accessors(chain = true)
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
@ExcelTarget("user")
@ContentRowHeight(50)
@ColumnWidth(20)
@ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
@HeadStyle(horizontalAlignment = HorizontalAlignment.CENTER)
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Excel(name = "用户名", orderNum = "4", width = 8)
    @ExcelProperty(value = "用户名", index = 3)
    private String name;

    /**
     * 年龄
     */
    @Excel(name = "年龄", orderNum = "5", width = 10, suffix = "岁")
    @ExcelProperty(value = "年龄", index = 4)
    private Integer age;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱", orderNum = "6", width = 20)
    @ExcelProperty(value = "邮箱", index = 5)
    @ColumnWidth(30)
    private String email;

    /**
     * 数据库最好使用int类型;虽然高版本mybatis-plus可以识别数据库tinyint类型的枚举
     */
    @Excel(name = "性别", orderNum = "7", width = 6)
    @com.alibaba.excel.annotation.ExcelIgnore
    private SexEnum gender;

    /**
     * 租户id
     */
    @ExcelIgnore
    @com.alibaba.excel.annotation.ExcelIgnore
    private Long tenantId;

    @ExcelIgnore
    @TableField(exist = false)
    @com.alibaba.excel.annotation.ExcelIgnore
    private List<Long> ids;
}
