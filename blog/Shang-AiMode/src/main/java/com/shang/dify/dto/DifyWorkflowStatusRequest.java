package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Dify 工作流状态查询请求 DTO
 *
 * @author shihang.shang
 * @className DifyWorkflowStatusRequest
 * @date 2025-01-28 15:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Dify 工作流状态查询请求参数")
public class DifyWorkflowStatusRequest extends BaseDifyRequest {

    @ApiModelProperty(value = "工作流运行ID", required = true, example = "3c90c3cc-0d44-4b50-8888-8dd25736052a")
    private String workflowRunId;
}
