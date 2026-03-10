package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Dify API 基础请求类
 * 包含所有 Dify API 请求的通用参数
 *
 * @author shihang.shang
 * @className BaseDifyRequest
 * @date 2025-01-28 15:00
 */
@Data
@ApiModel(description = "Dify API 基础请求参数")
public class BaseDifyRequest {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", required = true, example = "admin")
    private Long userId;

    /**
     * 资源ID（知识库ID或工作流ID）
     */
    @ApiModelProperty(value = "资源ID", required = true, example = "knowledge_base_001")
    private String resourceId;

    /**
     * 密钥类型
     */
    @ApiModelProperty(value = "密钥类型", required = true, example = "dataset", allowableValues = "dataset,workflow")
    private String keyType;
}
