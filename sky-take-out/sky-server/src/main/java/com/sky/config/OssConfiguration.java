package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * oss配置类，用于创建AliOssUtil对象
 *
 * @author xyzZero3
 * @date 2024/9/6 23:15
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Autowired
    AliOssProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil() {
        return new AliOssUtil(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret(),
                properties.getBucketName());
    }

}
