package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Dify Workflow 请求 DTO
 *
 * @author shihang.shang
 * @className DifyWorkflowRequest
 * @date 2025-01-28 10:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Dify Workflow 请求参数")
public class DifyWorkflowRequest extends BaseDifyRequest {

    @ApiModelProperty(value = "工作流输入参数", required = true)
    private Map<String, Object> inputs;

    @ApiModelProperty(value = "响应模式", example = "blocking", allowableValues = "blocking,streaming")
    @com.fasterxml.jackson.annotation.JsonProperty("response_mode")
    private String responseMode = "blocking";

    @ApiModelProperty(value = "用户标识", example = "workflow_user_001")
    private String user = "admin";

}
