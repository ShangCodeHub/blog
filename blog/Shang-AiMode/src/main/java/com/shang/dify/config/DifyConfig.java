package com.shang.dify.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Dify API 配置类
 * 用于配置 Dify API 的基础信息
 * 
 * @author shihang.shang
 * @since 2024-10-22
 */
@Data
@Configuration
@org.springframework.context.annotation.Lazy
public class DifyConfig {
    /**
     * Dify API 基础URL
     */
    @Value("${dify.base-url:}")
    private String baseUrl;
    /**
     * 私钥
     */
    @Value("${dify.private-url:}")
    private String privateUrl;
    /**
     * API Key (知识库)
     */
    @Value("${dify.api-key:}")
    private String apiKey;
    /**
     * 工作流 API Key
     */
    @Value("${dify.api-workflow-key:}")
    private String apiWorkflowKey;
    /**
     * 请求超时时间（毫秒）
     */
    @Value("${dify.timeout:30000}")
    private Integer timeout;
    /**
     * 连接超时时间（毫秒）
     */
    @Value("${dify.connect-timeout:10000}")
    private Integer connectTimeout;
    /**
     * 重试次数
     */
    @Value("${dify.retry-count:3}")
    private Integer retryCount;
    /**
     * 是否启用重试
     */
    @Value("${dify.enable-retry:true}")
    private Boolean enableRetry;
}