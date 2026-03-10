package com.shang.dify.dto;

import lombok.Data;

import java.util.Map;

/**
 * Dify 工作流运行状态 DTO
 *
 * @author shihang.shang
 * @className DifyWorkflowRunStatus
 * @date 2025-01-28 11:00
 */
@Data
public class DifyWorkflowRunStatus {

    private String id;

    private String workflowId;

    private String status;

    private String inputs;

    private Map<String, Object> outputs;

    private String error;

    private Integer totalSteps;

    private Integer totalTokens;

    private Long createdAt;

    private Long finishedAt;

    private Double elapsedTime;
}
