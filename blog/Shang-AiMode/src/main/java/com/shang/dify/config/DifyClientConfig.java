package com.shang.dify.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Dify 客户端配置类
 * 配置 RestTemplate、ObjectMapper 和 WebClient Bean
 *
 * @author shihang.shang
 * @since 2024-10-22
 */
@Configuration
@Lazy
public class DifyClientConfig {
    
    @Autowired(required = false)
    private DifyConfig difyConfig;

    /**
     * 创建 RestTemplate Bean
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    /**
     * 配置 HTTP 请求工厂
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 设置连接超时时间（如果配置了的话）
        if (difyConfig != null && difyConfig.getConnectTimeout() != null) {
            factory.setConnectTimeout(difyConfig.getConnectTimeout());
        } else {
            factory.setConnectTimeout(30000); // 默认30秒（知识库上传可能需要更长时间）
        }

        // 设置读取超时时间（如果配置了的话）
        // 知识库上传操作可能需要较长时间处理，设置更长的读取超时
        if (difyConfig != null && difyConfig.getTimeout() != null) {
            factory.setReadTimeout(difyConfig.getTimeout());
        } else {
            factory.setReadTimeout(120000); // 默认120秒（2分钟，知识库上传可能需要索引处理）
        }

        return factory;
    }

    /**
     * 创建 ObjectMapper Bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}

