package com.shang.dify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dify 知识库检索请求 DTO
 * 用于封装检索知识库的请求参数
 *
 * @author shihang.shang
 * @since 2024-10-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifyRetrieveRequest extends BaseDifyRequest {
    /**
     * 检索关键词
     */
    private String query;
}