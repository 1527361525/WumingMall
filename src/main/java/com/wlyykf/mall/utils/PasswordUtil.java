package com.wlyykf.mall.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 密码加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 密码匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 密码匹配结果
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

}
