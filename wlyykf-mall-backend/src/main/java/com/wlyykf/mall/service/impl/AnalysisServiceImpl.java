package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.config.AbnormalMonitorConfig;
import com.wlyykf.mall.mappers.AnalysisMapper;
import com.wlyykf.mall.service.AnalysisService;
import com.wlyykf.mall.utils.IpRegionUtil;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Resource
    private AbnormalMonitorConfig abnormalMonitorConfig;

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
                    // 将 yearweek 格式从 yyyyww 转换为 yyyy年ww周
                    for (Map<String, Object> item : result) {
                        Object yearweek = item.get("yearweek");
                        if (yearweek != null) {
                            String yw = String.valueOf(yearweek);
                            if (yw.length() == 6) {
                                String year = yw.substring(0, 4);
                                String week = yw.substring(4);
                                item.put("yearweek", year + "年" + week);
                                item.put("week", year + "年" + week);
                            }
                        }
                    }
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

    @Override
    public ResponseVO<Map<String, Object>> getSalesAbnormalDetection() {
        try {
            // 获取今日实时数据
            Map<String, Object> todayData = analysisMapper.getTodayRealtimeSales();
            // 获取昨日同期数据
            Map<String, Object> yesterdayData = analysisMapper.getYesterdaySamePeriodSales();
            // 获取上周同日数据
            Map<String, Object> lastWeekData = analysisMapper.getLastWeekSameDaySales();

            // 提取数值
            long todayOrderCount = extractLongValue(todayData.get("order_count"));
            double todaySalesAmount = extractDoubleValue(todayData.get("sales_amount"));

            long yesterdayOrderCount = extractLongValue(yesterdayData.get("order_count"));
            double yesterdaySalesAmount = extractDoubleValue(yesterdayData.get("sales_amount"));

            long lastWeekOrderCount = extractLongValue(lastWeekData.get("order_count"));
            double lastWeekSalesAmount = extractDoubleValue(lastWeekData.get("sales_amount"));

            // 计算环比（今日 vs 昨日同期）波动率
            double orderCountChangeRate = calculateChangeRate(todayOrderCount, yesterdayOrderCount);
            double salesAmountChangeRate = calculateChangeRate(todaySalesAmount, yesterdaySalesAmount);

            // 计算同比（今日 vs 上周同日）波动率
            double orderCountChangeRateWeek = calculateChangeRate(todayOrderCount, lastWeekOrderCount);
            double salesAmountChangeRateWeek = calculateChangeRate(todaySalesAmount, lastWeekSalesAmount);

            // 获取阈值
            double threshold = abnormalMonitorConfig.getThreshold();

            // 判断是否异常（波动超过阈值）
            boolean isOrderCountAbnormal = Math.abs(orderCountChangeRate) >= threshold;
            boolean isSalesAmountAbnormal = Math.abs(salesAmountChangeRate) >= threshold;
            boolean isOrderCountAbnormalWeek = Math.abs(orderCountChangeRateWeek) >= threshold;
            boolean isSalesAmountAbnormalWeek = Math.abs(salesAmountChangeRateWeek) >= threshold;

            // 构建结果
            Map<String, Object> result = new HashMap<>();

            // 今日数据
            Map<String, Object> todayMap = new HashMap<>();
            todayMap.put("orderCount", todayOrderCount);
            todayMap.put("salesAmount", todaySalesAmount);
            result.put("today", todayMap);

            // 昨日同期数据
            Map<String, Object> yesterdayMap = new HashMap<>();
            yesterdayMap.put("orderCount", yesterdayOrderCount);
            yesterdayMap.put("salesAmount", yesterdaySalesAmount);
            result.put("yesterday", yesterdayMap);

            // 上周同日数据
            Map<String, Object> lastWeekMap = new HashMap<>();
            lastWeekMap.put("orderCount", lastWeekOrderCount);
            lastWeekMap.put("salesAmount", lastWeekSalesAmount);
            result.put("lastWeekSameDay", lastWeekMap);

            // 环比波动（今日 vs 昨日）
            Map<String, Object> dayChangeMap = new HashMap<>();
            dayChangeMap.put("orderCountChangeRate", roundToTwoDecimals(orderCountChangeRate));
            dayChangeMap.put("salesAmountChangeRate", roundToTwoDecimals(salesAmountChangeRate));
            dayChangeMap.put("isOrderCountAbnormal", isOrderCountAbnormal);
            dayChangeMap.put("isSalesAmountAbnormal", isSalesAmountAbnormal);
            result.put("dayOverDayChange", dayChangeMap);

            // 同比波动（今日 vs 上周同日）
            Map<String, Object> weekChangeMap = new HashMap<>();
            weekChangeMap.put("orderCountChangeRate", roundToTwoDecimals(orderCountChangeRateWeek));
            weekChangeMap.put("salesAmountChangeRate", roundToTwoDecimals(salesAmountChangeRateWeek));
            weekChangeMap.put("isOrderCountAbnormal", isOrderCountAbnormalWeek);
            weekChangeMap.put("isSalesAmountAbnormal", isSalesAmountAbnormalWeek);
            result.put("weekOverWeekChange", weekChangeMap);

            // 异常检测阈值
            result.put("threshold", threshold);

            // 总体异常状态
            boolean hasAbnormal = isOrderCountAbnormal || isSalesAmountAbnormal ||
                    isOrderCountAbnormalWeek || isSalesAmountAbnormalWeek;
            result.put("hasAbnormal", hasAbnormal);

            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取销售异常检测结果失败", e);
            return ResponseVO.fail("获取销售异常检测结果失败", null);
        }
    }

    @Override
    public ResponseVO<Map<String, Object>> getTodayRealtimeData() {
        try {
            Map<String, Object> todayData = analysisMapper.getTodayRealtimeSales();

            // 处理数值类型
            long orderCount = extractLongValue(todayData.get("order_count"));
            double salesAmount = extractDoubleValue(todayData.get("sales_amount"));

            Map<String, Object> result = new HashMap<>();
            result.put("orderCount", orderCount);
            result.put("salesAmount", salesAmount);
            result.put("date", LocalDate.now().toString());

            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("获取今日实时数据失败", e);
            return ResponseVO.fail("获取今日实时数据失败", null);
        }
    }

    /**
     * 计算变化率（百分比）
     * @param current 当前值
     * @param previous 对比值
     * @return 变化率，单位：%
     */
    private double calculateChangeRate(double current, double previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((current - previous) / previous) * 100;
    }

    /**
     * 从对象中提取 Long 值
     */
    private long extractLongValue(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    /**
     * 从对象中提取 Double 值
     */
    private double extractDoubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    /**
     * 保留两位小数
     */
    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
