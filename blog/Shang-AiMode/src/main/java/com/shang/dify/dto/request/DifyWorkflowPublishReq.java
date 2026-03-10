package com.shang.dify.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Dify 工作流发布请求
 *
 * @author JiaWen.Wu
 * @className DifyWorkflowPublishReq
 * @date 2025-12-08 16:00
 */
@Data
public class DifyWorkflowPublishReq {

    /**
     * 工作流配置的 hash 值（更新后的 hash）
     */
    @JsonProperty("hash")
    private String hash;

    /**
     * 标记名称
     */
    @JsonProperty("marked_name")
    private String markedName;

    /**
     * 标记注释
     */
    @JsonProperty("marked_comment")
    private String markedComment;
}

