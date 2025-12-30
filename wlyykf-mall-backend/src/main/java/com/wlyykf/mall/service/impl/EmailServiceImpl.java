package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.component.RedisComponent;
import com.wlyykf.mall.service.EmailService;
import com.wlyykf.mall.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public void sendCode(String email) {
        // 生成四位随机数字
        String code = String.valueOf((int) (Math.random() * 10000));

        // 存入redis
        redisComponent.saveCheckCode(code, email);

        log.info("向邮箱 " + email + " 发送验证码：" + code);

        sendSimpleMail(email, "无名商城验证码", "您的验证码是：" + code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String checkCode = redisComponent.getCheckCode(email);
        return Objects.equals(checkCode, code);
    }

    @Override
    public void sendDeliveryEmail(String email, String orderNo) {
        sendSimpleMail(email, "发货通知", "您的订单 " + orderNo + " 已发货，请耐心等待");
    }

    /**
     * 发送简单文本邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    private void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

}
