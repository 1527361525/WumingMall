package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.mappers.AnalysisMapper;
import com.wlyykf.mall.service.AnalysisService;
import com.wlyykf.mall.utils.IpRegionUtil;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Override
    public ResponseVO<List<Map<String, Object>>> getSalesTrend(String type) {
        try {
            List<Map<String, Object>> result;
            switch (type.toLowerCase()) {
                case "day":
                    result = analysisMapper.getSalesTrendDaily();
                    result = fillMissingDailyData(result, 7);
                    break;
                case "week":
                    result = analysisMapper.getSalesTrendWeekly();
                    break;
                case "month":
                    result = analysisMapper.getSalesTrendMonthly();
                    break;
                default:
                    return ResponseVO.fail("无效的时间类型，支持：day/week/month", null);
            }
            
            // 处理数值类型转换
            for (Map<String, Object> item : result) {
                Object amount = item.get("sales_amount");
                if (amount instanceof BigDecimal) {
                    item.put("sales_amount", ((BigDecimal) amount).doubleValue());
                }
            }
            
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取销售趋势失败", e);
            return ResponseVO.fail("获取销售趋势失败", null);
        }
    }

    @Override
    public ResponseVO<List<Map<String, Object>>> getProductTrend(Long productId, String type) {
        try {
            List<Map<String, Object>> result;
            switch (type.toLowerCase()) {
                case "day":
                    result = analysisMapper.getProductTrendDaily(productId);
                    result = fillMissingDailyData(result, 7);
                    break;
                case "week":
                    result = analysisMapper.getProductTrendWeekly(productId);
                    break;
                case "month":
                    result = analysisMapper.getProductTrendMonthly(productId);
                    break;
                default:
                    return ResponseVO.fail("无效的时间类型，支持：day/week/month", null);
            }
            
            // 处理数值类型转换
            for (Map<String, Object> item : result) {
                Object amount = item.get("sales_amount");
                if (amount instanceof BigDecimal) {
                    item.put("sales_amount", ((BigDecimal) amount).doubleValue());
                }
            }
            
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取商品销售趋势失败", e);
            return ResponseVO.fail("获取商品销售趋势失败", null);
        }
    }

    @Override
    public ResponseVO<List<Map<String, Object>>> getCategorySales() {
        try {
            List<Map<String, Object>> result = analysisMapper.getCategorySalesStats();
            
            // 处理数值类型转换
            for (Map<String, Object> item : result) {
                Object amount = item.get("sales_amount");
                if (amount instanceof BigDecimal) {
                    item.put("sales_amount", ((BigDecimal) amount).doubleValue());
                }
                // 确保数值不为null
                if (item.get("order_count") == null) {
                    item.put("order_count", 0);
                }
                if (item.get("sales_quantity") == null) {
                    item.put("sales_quantity", 0);
                }
                if (item.get("sales_amount") == null) {
                    item.put("sales_amount", 0.0);
                }
            }
            
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取类别销售统计失败", e);
            return ResponseVO.fail("获取类别销售统计失败", null);
        }
    }

    /**
     * 填充缺失的日期数据（对于没有销售记录的天数补0）
     * @param data 原始数据
     * @param days 天数
     * @return 填充后的数据
     */
    private List<Map<String, Object>> fillMissingDailyData(List<Map<String, Object>> data, int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        
        // 将已有数据放入Map
        if (data != null) {
            for (Map<String, Object> item : data) {
                String date = (String) item.get("date");
                if (date != null) {
                    dataMap.put(date, item);
                }
            }
        }
        
        // 生成最近days天的日期列表
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.format(formatter);
            
            Map<String, Object> item = dataMap.get(dateStr);
            if (item == null) {
                // 补充缺失的日期数据
                item = new HashMap<>();
                item.put("date", dateStr);
                item.put("order_count", 0);
                item.put("sales_amount", 0.0);
                item.put("sales_quantity", 0);
            }
            result.add(item);
        }
        
        return result;
    }
}
