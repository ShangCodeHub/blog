package com.shang.dify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dify 数据集请求DTO
 * 根据 Dify API 文档设计
 * 
 * @author shihang.shang
 * @since 2024-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifyDatasetRequest extends BaseDifyRequest {
    /**
     * 数据集名称
     */
    private String name;
    /**
     * 数据集描述
     */
    private String description;
    /**
     * 权限设置
     */
    @Builder.Default
    private String permission = "only_me";
    /**
     * 索引技术
     */
    @Builder.Default
    private String indexing_technique = "high_quality";
}
