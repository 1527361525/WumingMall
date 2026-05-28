package com.wlyykf.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.dto.OperationLogQueryDTO;
import com.wlyykf.mall.entity.OperationLog;
import com.wlyykf.mall.entity.UserBrowseLog;
import com.wlyykf.mall.mappers.OperationLogMapper;
import com.wlyykf.mall.mappers.UserBrowseLogMapper;
import com.wlyykf.mall.service.LogService;
import com.wlyykf.mall.vo.OperationLogVO;
import com.wlyykf.mall.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PageResultVO<OperationLogVO> getOperationLogList(OperationLogQueryDTO queryDTO) {
        // 构建分页对象
        Page<OperationLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getDelFlag, 0);

        // 按用户ID筛选
        if (queryDTO.getUserId() != null) {
            wrapper.eq(OperationLog::getUserId, queryDTO.getUserId());
        }

        // 按操作类型筛选
        if (queryDTO.getOperationType() != null && !queryDTO.getOperationType().isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, queryDTO.getOperationType());
        }

        // 按日期范围筛选
        if (queryDTO.getStartDate() != null) {
            LocalDateTime startDateTime = queryDTO.getStartDate().atStartOfDay();
            wrapper.ge(OperationLog::getOperationTime, startDateTime);
        }

        if (queryDTO.getEndDate() != null) {
            LocalDateTime endDateTime = queryDTO.getEndDate().atTime(LocalTime.MAX);
            wrapper.le(OperationLog::getOperationTime, endDateTime);
        }

        // 按操作时间倒序排列
        wrapper.orderByDesc(OperationLog::getOperationTime);

        // 执行查询
        IPage<OperationLog> resultPage = operationLogMapper.selectPage(page, wrapper);

        // 转换为VO
        List<OperationLogVO> voList = resultPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());

        // 构建返回结果
        PageResultVO<OperationLogVO> resultVO = new PageResultVO<>();
        resultVO.setTotal(resultPage.getTotal());
        resultVO.setData(voList);
        resultVO.setCode(PageResultVO.SUCCESS);
        resultVO.setInfo("查询成功");

        return resultVO;
    }

    /**
     * 将OperationLog实体转换为OperationLogVO
     */
    private OperationLogVO convertToVO(OperationLog operationLog) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(operationLog, vo);
        // 将Long类型转换为String，避免前端精度丢失
        if (operationLog.getLogId() != null) {
            vo.setLogId(String.valueOf(operationLog.getLogId()));
        }
        if (operationLog.getUserId() != null) {
            vo.setUserId(String.valueOf(operationLog.getUserId()));
        }
        return vo;
    }
}
