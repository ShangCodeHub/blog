package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Dify 工作流日志查询请求 DTO
 *
 * @author shihang.shang
 * @className DifyWorkflowLogsRequest
 * @date 2025-01-28 15:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Dify 工作流日志查询请求参数")
public class DifyWorkflowLogsRequest extends BaseDifyRequest {

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页数量", example = "20")
    private Integer limit = 20;
}
