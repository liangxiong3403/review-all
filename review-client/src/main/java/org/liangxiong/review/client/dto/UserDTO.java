package org.liangxiong.review.client.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2021-03-27 15:06
 * @description 不能使用@Accessors(chain=true),否则数据读取失败
 **/
@Data
public class UserDTO {

    /**
     * 用户名
     */
    @Excel(name = "用户名", orderNum = "1")
    private String name;

    /**
     * 年龄
     */
    @Excel(name = "年龄", orderNum = "2")
    private Integer age;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱", orderNum = "3")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
}
