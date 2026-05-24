package com.wlyykf.mall.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * IP地址解析工具类
 * 使用ip2region离线库解析IP地址对应的地域信息
 */
@Slf4j
@Component
public class IpRegionUtil {

    private Searcher searcher;
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            // 加载ip2region.xdb文件
            ClassPathResource resource = new ClassPathResource("ip2region.xdb");
            byte[] cBuff = Searcher.loadContentFromFile(resource.getFile().getAbsolutePath());
            searcher = Searcher.newWithBuffer(cBuff);
            initialized = true;
            log.info("IpRegionUtil初始化成功，ip2region.xdb加载完成");
        } catch (Exception e) {
            log.error("IpRegionUtil初始化失败: {}", e.getMessage());
            initialized = false;
        }
    }

    @PreDestroy
    public void destroy() {
        if (searcher != null) {
            try {
                searcher.close();
            } catch (IOException e) {
                log.error("关闭IpRegionUtil失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 解析IP地址获取完整地域信息
     *
     * @param ip IP地址
     * @return 地域信息字符串，格式：国家|区域|省份|城市|ISP
     *         例如：中国|0|广东省|广州市|电信
     */
    public String getRegion(String ip) {
        if (!initialized || searcher == null) {
            log.warn("IpRegionUtil未初始化，无法解析IP: {}", ip);
            return "";
        }
        if (ip == null || ip.isEmpty() || "127.0.0.1".equals(ip) || ip.startsWith("192.168.") || ip.startsWith("10.")) {
            return "";
        }
        try {
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            log.debug("IP解析成功: {} -> {}, 耗时: {}μs", ip, region, cost);
            return region;
        } catch (Exception e) {
            log.error("IP解析失败: {}, 错误: {}", ip, e.getMessage());
            return "";
        }
    }

    /**
     * 获取省份信息
     *
     * @param ip IP地址
     * @return 省份名称，如"广东省"
     */
    public String getProvince(String ip) {
        String region = getRegion(ip);
        if (region == null || region.isEmpty()) {
            return "未知";
        }
        // ip2region返回格式：国家|区域|省份|城市|ISP
        String[] parts = region.split("\\|");
        if (parts.length >= 3 && !parts[2].isEmpty() && !"0".equals(parts[2])) {
            return parts[2];
        }
        return "未知";
    }

    /**
     * 获取城市信息
     *
     * @param ip IP地址
     * @return 城市名称，如"广州市"
     */
    public String getCity(String ip) {
        String region = getRegion(ip);
        if (region == null || region.isEmpty()) {
            return "未知";
        }
        String[] parts = region.split("\\|");
        if (parts.length >= 4 && !parts[3].isEmpty() && !"0".equals(parts[3])) {
            return parts[3];
        }
        return "未知";
    }
}
