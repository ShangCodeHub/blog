package com.shang.dify.dto.request;

import lombok.Data;

import java.util.List;

/**
 * Dify 工作流更新并发布请求
 * 综合接口：先获取工作流详情，然后更新，最后发布
 *
 * @author JiaWen.Wu
 * @className DifyWorkflowUpdateAndPublishReq
 * @date 2025-12-08 16:00
 */
@Data
public class DifyWorkflowUpdateAndPublishReq {

    /**
     * 工作流ID（Dify 应用ID）
     */
    private String appId;

    /**
     * 新的知识库ID列表（用于更新 knowledge-retrieval 节点的 dataset_ids）
     */
    private List<String> datasetIds;

    /**
     * 标记名称（发布时使用）
     */
    private String markedName;

    /**
     * 标记注释（发布时使用）
     */
    private String markedComment;
}

