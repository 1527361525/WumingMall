package com.wlyykf.mall.interceptor;

import com.wlyykf.mall.component.RedisComponent;
import com.wlyykf.mall.constants.Constants;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private RedisComponent redisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 如果是获取图片的请求，则不进行token验证
        if(request.getRequestURI().contains("/file")){
            return true;
        }

        String header = request.getHeader("Authorization");
        if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            throw new BusinessException(401, "Token错误");
        }

        // 提取token
        String token = header.substring(7);

        // 查看Redis中是否有该token
        TokenUserInfoDTO tokenUserInfoDTO = redisComponent.getTokenInfo(token);
        if (tokenUserInfoDTO == null) {
            throw new BusinessException(401, "Token已过期");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
