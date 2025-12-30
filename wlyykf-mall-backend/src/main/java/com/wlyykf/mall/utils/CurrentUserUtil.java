package com.wlyykf.mall.utils;

import com.wlyykf.mall.component.RedisComponent;
import com.wlyykf.mall.constants.Constants;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
public class CurrentUserUtil {
    @Resource
    private RedisComponent redisComponent;

    public TokenUserInfoDTO getCurrentUserInfo() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        TokenUserInfoDTO tokenUserInfoDTO = redisComponent.getTokenInfo(token);
        if (Objects.isNull(tokenUserInfoDTO)) {
            throw new BusinessException(401, "Token错误");
        }
        return redisComponent.getTokenInfo(token);
    }

}
