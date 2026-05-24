package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.mappers.AnalysisMapper;
import com.wlyykf.mall.service.AnalysisService;
import com.wlyykf.mall.utils.IpRegionUtil;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现类
 */
@Service
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {

    @Resource
    private AnalysisMapper analysisMapper;

    @Resource
    private IpRegionUtil ipRegionUtil;

    @Override
    public ResponseVO<List<Map<String, Object>>> getUserRegionDistribution() {
        try {
            // 查询所有有登录IP的用户
            List<Map<String, Object>> userIpList = analysisMapper.getUserLastLoginIps();
            
            if (userIpList == null || userIpList.isEmpty()) {
                return ResponseVO.success(Collections.emptyList());
            }

            // 统计各省份用户数量
            Map<String, Integer> regionCountMap = new HashMap<>();
            int unknownCount = 0;

            for (Map<String, Object> userIp : userIpList) {
                String ip = (String) userIp.get("last_login_ip");
                if (ip == null || ip.isEmpty()) {
                    unknownCount++;
                    continue;
                }
                
                String province = ipRegionUtil.getProvince(ip);
                if ("未知".equals(province)) {
                    unknownCount++;
                } else {
                    regionCountMap.merge(province, 1, Integer::sum);
                }
            }

            // 转换为列表并排序
            List<Map<String, Object>> result = regionCountMap.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("province", entry.getKey());
                        map.put("count", entry.getValue());
                        return map;
                    })
                    .sorted((a, b) -> ((Integer) b.get("count")).compareTo((Integer) a.get("count")))
                    .collect(Collectors.toList());

            // 添加未知地区统计
            if (unknownCount > 0) {
                Map<String, Object> unknownMap = new HashMap<>();
                unknownMap.put("province", "未知");
                unknownMap.put("count", unknownCount);
                result.add(unknownMap);
            }

            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取用户地域分布失败", e);
            return ResponseVO.fail("获取用户地域分布失败", null);
        }
    }

    @Override
    public ResponseVO<List<Map<String, Object>>> getUserPurchasingPower() {
        try {
            List<Map<String, Object>> result = analysisMapper.getUserPurchasingPowerDistribution();
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取用户购买力分层失败", e);
            return ResponseVO.fail("获取用户购买力分层失败", null);
        }
    }

    @Override
    public ResponseVO<List<Map<String, Object>>> getUserPreference(Long userId) {
        try {
            List<Map<String, Object>> result = analysisMapper.getUserCategoryPreference(userId);
            if (result == null || result.isEmpty()) {
                return ResponseVO.success("该用户暂无购买记录", Collections.emptyList());
            }
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取用户偏好分类失败", e);
            return ResponseVO.fail("获取用户偏好分类失败", null);
        }
    }
}
