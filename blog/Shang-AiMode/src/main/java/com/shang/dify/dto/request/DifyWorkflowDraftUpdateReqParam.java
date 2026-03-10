package com.shang.dify.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Dify 工作流 Draft 配置更新请求参数
 *
 * @author JiaWen.Wu
 * @className DifyWorkflowDraftUpdateReqParam
 * @date 2025-12-08 15:30
 */
@Data
@ApiModel(description = "Dify 工作流 Draft 配置更新请求参数")
public class DifyWorkflowDraftUpdateReqParam {

    /**
     * 工作流ID（Dify 应用ID）
     */
    @ApiModelProperty(value = "工作流ID（Dify 应用ID）", example = "158014eb-6b53-417e-898c-e5feba1de72e")
    private String appId;

    /**
     * 新的知识库ID列表（用于更新 knowledge-retrieval 节点的 dataset_ids）
     */
    @ApiModelProperty(value = "新的知识库ID列表（用于更新 knowledge-retrieval 节点的 dataset_ids）", example = "[\"6866e4ef-91ba-492c-a91b-5a76dabea0f9\"]")
    private List<String> datasetIds;
}

