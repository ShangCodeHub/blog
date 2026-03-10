package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Dify 工作流日志响应 DTO
 *
 * @author shihang.shang
 * @className DifyWorkflowLogsResponse
 * @date 2025-01-28 11:30
 */
@Data
@ApiModel(description = "Dify 工作流日志响应")
public class DifyWorkflowLogsResponse {

    @ApiModelProperty(value = "当前页码")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer limit;

    @ApiModelProperty(value = "总数量")
    private Integer total;

    @ApiModelProperty(value = "是否有更多数据")
    private Boolean hasMore;

    @ApiModelProperty(value = "日志数据列表")
    private List<WorkflowLogItem> data;

    @Data
    @ApiModel(description = "工作流日志项")
    public static class WorkflowLogItem {

        @ApiModelProperty(value = "日志ID")
        private String id;

        @ApiModelProperty(value = "工作流运行信息")
        private WorkflowRunInfo workflowRun;

        @ApiModelProperty(value = "创建来源")
        private String createdFrom;

        @ApiModelProperty(value = "创建者角色")
        private String createdByRole;

        @ApiModelProperty(value = "创建者账户ID")
        private String createdByAccount;

        @ApiModelProperty(value = "创建者终端用户信息")
        private CreatedByEndUser createdByEndUser;

        @ApiModelProperty(value = "创建时间")
        private Long createdAt;
    }

    @Data
    @ApiModel(description = "工作流运行信息")
    public static class WorkflowRunInfo {

        @ApiModelProperty(value = "运行ID")
        private String id;

        @ApiModelProperty(value = "版本")
        private String version;

        @ApiModelProperty(value = "状态")
        private String status;

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
    @ApiModel(description = "创建者终端用户信息")
    public static class CreatedByEndUser {

        @ApiModelProperty(value = "用户ID")
        private String id;

        @ApiModelProperty(value = "用户类型")
        private String type;

        @ApiModelProperty(value = "是否匿名")
        private Boolean isAnonymous;

        @ApiModelProperty(value = "会话ID")
        private String sessionId;
    }
}
