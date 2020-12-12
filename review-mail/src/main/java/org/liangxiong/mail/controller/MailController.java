package org.liangxiong.mail.controller;

import org.liangxiong.mail.vo.MailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-19 09:10
 * @description 发送邮件
 **/
@RequestMapping("/mail")
@RestController
public class MailController {

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping("/send")
    public void sendMail(@RequestBody MailVO mailVO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailVO.getFrom());
        message.setTo(mailVO.getTo());
        message.setCc(mailVO.getCc());
        message.setSubject(mailVO.getSubject());
        message.setText(mailVO.getText());
        message.setSentDate(mailVO.getSentDate());
        emailSender.send(message);
    }
}
