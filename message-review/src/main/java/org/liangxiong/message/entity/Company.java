package org.liangxiong.message.entity;

import lombok.*;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-07-31 21:29
 * @description 公司
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Company {

    /**
     * 名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 排名
     */
    private Integer rank;
}
