package com.wlyykf.mall.aop;

import com.wlyykf.mall.annotation.OperationLog;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.service.LogService;
import com.wlyykf.mall.utils.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面
 * 拦截带有 @OperationLog 注解的方法，自动记录操作日志
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {

    private final LogService logService;
    private final CurrentUserUtil currentUserUtil;

    /**
     * 定义切点：所有带有 @OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.wlyykf.mall.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 方法执行成功后记录操作日志
     */
    @AfterReturning("operationLogPointcut()")
    public void recordOperationLog(JoinPoint joinPoint) {
        try {
            // 获取当前登录用户信息
            TokenUserInfoDTO currentUser = currentUserUtil.getCurrentUserInfo();
            if (currentUser == null) {
                log.debug("未登录用户操作，不记录操作日志");
                return;
            }

            // 获取方法上的注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLogAnnotation = method.getAnnotation(OperationLog.class);

            // 获取操作类型和内容
            String operationType = operationLogAnnotation.operationType();
            String operationContent = operationLogAnnotation.operationContent();

            // 如果操作内容为空，尝试从方法参数构建
            if (operationContent.isEmpty()) {
                operationContent = buildOperationContent(joinPoint);
            }

            // 获取IP地址
            String ipAddress = getClientIp();

            // 记录操作日志
            logService.recordOperationLog(
                    currentUser.getUserId(),
                    currentUser.getRole(),
                    operationType,
                    operationContent,
                    ipAddress
            );

            log.debug("记录操作日志成功: userId={}, operationType={}", currentUser.getUserId(), operationType);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    /**
     * 从方法参数构建操作内容
     */
    private String buildOperationContent(JoinPoint joinPoint) {
        StringBuilder content = new StringBuilder();
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    content.append(args[i].toString());
                    if (i < args.length - 1) {
                        content.append(", ");
                    }
                }
            }
        }

        return content.toString();
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "";
            }
            HttpServletRequest request = attributes.getRequest();

            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        } catch (Exception e) {
            log.error("获取客户端IP失败", e);
            return "";
        }
    }
}
