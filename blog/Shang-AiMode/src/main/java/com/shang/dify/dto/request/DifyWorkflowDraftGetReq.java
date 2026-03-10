package com.shang.dify.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

/**
 * Dify 工作流 Draft 配置获取请求
 *
 * @author JiaWen.Wu
 * @className DifyWorkflowDraftGetReq
 * @date 2025-12-08 15:30
 */
@Data
@ApiModel(description = "Dify 工作流 Draft 配置获取请求")
public class DifyWorkflowDraftGetReq {

    /**
     * 工作流ID（Dify 应用ID）
     */
    @ApiModelProperty(value = "工作流ID（Dify 应用ID）", example = "158014eb-6b53-417e-898c-e5feba1de72e")

    private String appId;
}

