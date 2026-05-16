package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.entity.OperationLog;
import com.wlyykf.mall.entity.UserBrowseLog;
import com.wlyykf.mall.mappers.OperationLogMapper;
import com.wlyykf.mall.mappers.UserBrowseLogMapper;
import com.wlyykf.mall.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final OperationLogMapper operationLogMapper;
    private final UserBrowseLogMapper userBrowseLogMapper;

    @Override
    @Async
    public void recordLoginLog(Long userId, Integer userType, String ip) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(userId);
            operationLog.setUserType(userType != null ? userType : 0);
            operationLog.setOperationType("LOGIN");
            operationLog.setOperationContent("用户登录");
            operationLog.setOperationTime(LocalDateTime.now());
            operationLog.setIpAddress(ip);
            operationLog.setCreateTime(LocalDateTime.now());
            operationLog.setUpdateTime(LocalDateTime.now());
            operationLog.setDelFlag(0);

            operationLogMapper.insert(operationLog);
            log.debug("记录用户登录日志成功: userId={}", userId);
        } catch (Exception e) {
            log.error("记录用户登录日志失败: userId={}", userId, e);
        }
    }

    @Override
    @Async
    public void recordLogoutLog(Long userId, Integer userType, String ip) {
        try {
            recordOperationLog(userId, userType, "LOGOUT", "用户登出", ip);
            log.debug("记录用户登出日志成功: userId={}", userId);
        } catch (Exception e) {
            log.error("记录用户登出日志失败: userId={}", userId, e);
        }
    }

    @Override
    @Async
    public void recordOperationLog(Long userId, Integer userType, String operationType, String operationContent, String ip) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(userId);
            operationLog.setUserType(userType != null ? userType : 0);
            operationLog.setOperationType(operationType);
            operationLog.setOperationContent(operationContent);
            operationLog.setOperationTime(LocalDateTime.now());
            operationLog.setIpAddress(ip);
            operationLog.setCreateTime(LocalDateTime.now());
            operationLog.setUpdateTime(LocalDateTime.now());
            operationLog.setDelFlag(0);

            operationLogMapper.insert(operationLog);
            log.debug("记录操作日志成功: userId={}, operationType={}", userId, operationType);
        } catch (Exception e) {
            log.error("记录操作日志失败: userId={}, operationType={}", userId, operationType, e);
        }
    }

    @Override
    @Async
    public void recordBrowseLog(Long userId, Long productId, Long categoryId, Integer stayDuration, String ip) {
        try {
            UserBrowseLog browseLog = new UserBrowseLog();
            browseLog.setUserId(userId);
            browseLog.setProductId(productId);
            browseLog.setCategoryId(categoryId);
            browseLog.setBrowseTime(LocalDateTime.now());
            browseLog.setStayDuration(stayDuration != null && stayDuration > 0 ? stayDuration : 1);
            browseLog.setIpAddress(ip);
            browseLog.setCreateTime(LocalDateTime.now());
            browseLog.setUpdateTime(LocalDateTime.now());
            browseLog.setDelFlag(0);

            userBrowseLogMapper.insert(browseLog);
            log.debug("记录浏览日志成功: userId={}, productId={}, stayDuration={}", userId, productId, stayDuration);
        } catch (Exception e) {
            log.error("记录浏览日志失败: userId={}, productId={}", userId, productId, e);
        }
    }
}
