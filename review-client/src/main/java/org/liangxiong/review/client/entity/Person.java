package org.liangxiong.review.client.entity;


import lombok.*;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-08-30 20:39
 * @description
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {

    private int id;

    private String title;

    private String firstName;

    private String lastName;

}
