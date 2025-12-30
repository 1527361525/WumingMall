package com.wlyykf.mall.component;

import com.wlyykf.mall.constants.Constants;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.utils.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class RedisComponent {

    @Resource
    private RedisUtil redisUtil;


    /**
     * 保存token信息
     * @param tokenUserInfoDTO token信息
     */
    public void saveTokenInfo(TokenUserInfoDTO tokenUserInfoDTO) {
        String token = UUID.randomUUID().toString();
        tokenUserInfoDTO.setExpireAt(System.currentTimeMillis() + Constants.ONE_DAY * 7);
        tokenUserInfoDTO.setToken(token);
        redisUtil.setex(Constants.REDIS_KEY_TOKEN + token, tokenUserInfoDTO, Constants.ONE_DAY * 7);
    }

    /**
     * 获取token信息
     * @param token token
     * @return token信息
     */
    public TokenUserInfoDTO getTokenInfo(String token) {
        return (TokenUserInfoDTO) redisUtil.get(Constants.REDIS_KEY_TOKEN + token);
    }

    /**
     * 删除token信息
     * @param token token
     */
    public void cleanToken(String token) {
        redisUtil.delete(Constants.REDIS_KEY_TOKEN + token);
    }

    /**
     * 保存验证码，有效期十分钟
     * @param code 验证码
     */
    public void saveCheckCode(String code, String email) {
        redisUtil.setex(Constants.REDIS_KEY_CHECK_CODE + email, code, Constants.ONE_MINUTE * 10);
    }

    /**
     * 获取验证码
     * @param email 邮箱
     * @return 验证码
     */
    public String getCheckCode(String email) {
        return (String) redisUtil.get(Constants.REDIS_KEY_CHECK_CODE + email);
    }

    /**
     * 删除验证码
     * @param checkCodeKey 验证码key
     */
    public void cleanCheckCode(String checkCodeKey) {
        redisUtil.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }
}
