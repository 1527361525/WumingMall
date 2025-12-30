package com.wlyykf.mall.service;

public interface EmailService {

    /**
     * 发送验证码
     * @param email 邮箱
     */
    void sendCode(String email);

    /**
     * 验证码验证
     * @param checkCodeKey redis中的code key
     * @param code 验证码
     * @return 验证结果
     */
    boolean verifyCode(String checkCodeKey, String code);

    /**
     * 发送电子邮件确认发货
     * @param email 收件人邮箱
     * @param orderNo 订单编号
     */
    void sendDeliveryEmail(String email, String orderNo);

}
