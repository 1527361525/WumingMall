package com.wlyykf.mall.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${admin.id}")
    private Long adminId;

    @Value("${project.folder}")
    private String projectFolder;
}
