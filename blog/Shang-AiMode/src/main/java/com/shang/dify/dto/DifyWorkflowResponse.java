package com.shang.dify.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Dify Workflow 响应 DTO
 *
 * @author shihang.shang
 * @className DifyWorkflowResponse
 * @date 2025-01-28 10:30
 */
@Data
@ApiModel(description = "Dify Workflow 响应")
public class DifyWorkflowResponse {

    private String taskId;

    private String workflowRunId;

    private WorkflowData data;

    @Data
    public static class WorkflowData {

        private String id;

        private String workflowId;

        private String status;

        private WorkflowOutputs outputs;

        @ApiModelProperty(value = "错误信息")
        private String error;

        @ApiModelProperty(value = "执行时间（秒）")
        private Double elapsedTime;

        @ApiModelProperty(value = "总Token数")
        private Integer totalTokens;

        @ApiModelProperty(value = "总步骤数")
        private Integer totalSteps;

        @ApiModelProperty(value = "创建时间")
        private Long createdAt;

        @ApiModelProperty(value = "完成时间")
        private Long finishedAt;
    }

    @Data
    @ApiModel(description = "工作流输出结果")
    public static class WorkflowOutputs {

        @ApiModelProperty(value = "生成的文件列表")
        private List<WorkflowFile> files;

        @ApiModelProperty(value = "文本输出")
        private String text;

        @ApiModelProperty(value = "JSON输出")
        private List<Map<String, Object>> json;
    }

    @Data
    @ApiModel(description = "工作流生成的文件")
    public static class WorkflowFile {

        @ApiModelProperty(value = "文件ID")
        private String id;

        @ApiModelProperty(value = "文件名")
        private String filename;

        @ApiModelProperty(value = "文件扩展名")
        private String extension;

        @ApiModelProperty(value = "MIME类型")
        private String mimeType;

        @ApiModelProperty(value = "文件大小（字节）")
        private Long size;

        @ApiModelProperty(value = "文件URL")
        private String url;
    }
}
