package com.wlyykf.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 销售异常监控配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "abnormal.monitor")
public class AbnormalMonitorConfig {

    /**
     * 异常检测阈值（百分比，默认30%）
     * 当日数据较昨日同期波动超过此阈值时标记为异常
     */
    private double threshold = 30.0;
}
