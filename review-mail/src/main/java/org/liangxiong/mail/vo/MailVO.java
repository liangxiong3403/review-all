package org.liangxiong.mail.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 09:21
 * @description 邮件参数封装
 **/
@Getter
@Setter
public class MailVO {

    /**
     * 发送方
     */
    private String from;
    /**
     * 接收方
     */
    private String to;
    /**
     * 抄送
     */
    private String cc;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 普通文本消息
     */
    private String text;
    /**
     * HTML格式消息
     */
    private String html;
    /**
     * 邮件发送时间
     */
    private Date sentDate;
}
