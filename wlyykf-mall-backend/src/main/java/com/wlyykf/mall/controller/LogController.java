package com.wlyykf.mall.controller;

import com.wlyykf.mall.dto.OperationLogQueryDTO;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.dto.UserBrowseLogDTO;
import com.wlyykf.mall.service.LogService;
import com.wlyykf.mall.utils.CurrentUserUtil;
import com.wlyykf.mall.vo.OperationLogVO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 日志控制器
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/log")
public class LogController {

    @Resource
    private LogService logService;

    @Resource
    private CurrentUserUtil currentUserUtil;

    @Resource
    private HttpServletRequest request;

    /**
     * 记录用户浏览日志
     *
     * @param browseLogDTO 浏览日志信息
     * @return 记录结果
     */
    @PostMapping("/browse")
    public ResponseVO<Void> recordBrowseLog(@Valid @RequestBody UserBrowseLogDTO browseLogDTO) {
        // 获取当前登录用户信息
        TokenUserInfoDTO currentUser = currentUserUtil.getCurrentUserInfo();

        // 只记录已登录用户的浏览行为
        if (currentUser == null) {
            log.debug("未登录用户浏览商品，不记录日志");
            return ResponseVO.success("未登录不记录日志", null);
        }

        try {
            logService.recordBrowseLog(
                    currentUser.getUserId(),
                    browseLogDTO.getProductId(),
                    browseLogDTO.getCategoryId(),
                    browseLogDTO.getStayDuration(),
                    getClientIp()
            );
            return ResponseVO.success("记录浏览日志成功", null);
        } catch (Exception e) {
            log.error("记录浏览日志失败", e);
            return ResponseVO.fail("记录浏览日志失败", null);
        }
    }

    /**
     * 分页查询操作日志列表（仅管理者可操作）
     *
     * @param queryDTO 查询条件
     * @return 操作日志分页列表
     */
    @PostMapping("/operation/list")
    public PageResultVO<OperationLogVO> getOperationLogList(@Valid @RequestBody OperationLogQueryDTO queryDTO) {
        // 校验当前用户是否为管理者
        TokenUserInfoDTO currentUser = currentUserUtil.getCurrentUserInfo();
        if (currentUser == null || currentUser.getRole() == null || currentUser.getRole() != 2) {
            PageResultVO<OperationLogVO> result = new PageResultVO<>();
            result.setCode(403);
            result.setInfo("仅管理者可操作");
            return result;
        }

        try {
            return logService.getOperationLogList(queryDTO);
        } catch (Exception e) {
            log.error("查询操作日志列表失败", e);
            PageResultVO<OperationLogVO> result = new PageResultVO<>();
            result.setCode(500);
            result.setInfo("查询操作日志列表失败");
            return result;
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
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
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
