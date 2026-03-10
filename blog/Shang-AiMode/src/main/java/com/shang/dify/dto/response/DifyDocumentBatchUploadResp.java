package com.shang.dify.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shang.dify.exception.BusinessException;
import com.shang.dify.common.vo.ResultCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dify 批量文档上传响应
 * 类型安全的批量文档上传响应定义
 * <p>
 * 字段名与 Dify API 响应保持一致，集中管理，避免硬编码
 * <p>
 * 根据 Dify API 文档，批量上传响应包含：
 * - document: 单个文档对象（第一个文件的信息，或文档列表中的第一个）
 * - batch: 批次ID（用于查询所有文件的状态）
 * - documents: 文档数组（可选，某些情况下可能直接返回所有文档）
 *

 * @author JiaWen.Wu
 * @className DifyDocumentBatchUploadResp
 * @date 2025-12-02 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyDocumentBatchUploadResp {
    private DifyDocumentInfo document;
    private String batch;
    private List<DifyDocumentInfo> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifyDocumentInfo {
        private String id;
        private String name;
        private Integer position;
        private String indexingStatus;
        private Long createdAt;
        private Integer tokens;
        private Integer wordCount;
        private String error;
    }

    /**
     * 从 JSON 字符串解析批量文档上传响应（类型安全）
     * <p>
     * 支持多种响应格式：
     * 1. 批量上传初始响应：{"document": {...}, "batch": "...", "documents": [...]}
     * 2. 批次状态查询响应：{"data": [...]} 或 {"batch": "...", "documents": [...]}
     *
     * @param jsonString   String JSON 字符串
     * @param objectMapper ObjectMapper Jackson 对象映射器
     * @return DifyDocumentBatchUploadResp 批量文档上传响应
     */

    public static DifyDocumentBatchUploadResp from(String jsonString, ObjectMapper objectMapper) throws Exception {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw BusinessException.of(ResultCode.SERVER_ERROR, "Dify API 响应体为空");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 解析 batch 字段
            String batch = extractString(rootNode, "batch");

            // 解析 documents 数组（优先使用 documents 字段）
            List<DifyDocumentInfo> documentsList = null;
            JsonNode documentsArray = rootNode.get("documents");
            if (documentsArray != null && documentsArray.isArray() && documentsArray.size() > 0) {
                documentsList = parseDocumentArray(documentsArray);
            }

            // 如果 documents 数组不存在，尝试解析 data 数组（批次状态查询接口返回格式）
            if (documentsList == null || documentsList.isEmpty()) {
                JsonNode dataArray = rootNode.get("data");
                if (dataArray != null && dataArray.isArray() && dataArray.size() > 0) {
                    documentsList = parseDocumentArray(dataArray);
                }
            }

            // 解析单个 document 对象（第一个文件的信息）
            DifyDocumentInfo document = null;
            JsonNode documentNode = rootNode.get("document");
            if (documentNode != null) {
                document = parseDocumentInfo(documentNode);
            }

            // 如果 documents 数组存在，优先使用第一个作为 document
            if (documentsList != null && !documentsList.isEmpty() && document == null) {
                document = documentsList.get(0);
            }

            return new DifyDocumentBatchUploadResp(document, batch, documentsList);

        } catch (JsonProcessingException e) {
            throw BusinessException.of(ResultCode.SERVER_ERROR, "解析 Dify 批量文档上传响应失败: %s", e.getMessage());
        }
    }

    /**
     * 解析文档数组
     *
     * @param arrayNode JsonNode 数组节点
     * @return List<DifyDocumentInfo> 文档列表
     */
    private static List<DifyDocumentInfo> parseDocumentArray(JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return null;
        }

        List<DifyDocumentInfo> documentsList = new ArrayList<DifyDocumentInfo>();
        for (JsonNode docNode : arrayNode) {
            DifyDocumentInfo docInfo = parseDocumentInfo(docNode);
            if (docInfo != null) {
                documentsList.add(docInfo);
            }
        }
        return documentsList.isEmpty() ? null : documentsList;
    }

    /**
     * 解析单个文档信息节点
     *
     * @param docNode JsonNode 文档节点
     * @return DifyDocumentInfo 文档信息，如果节点无效则返回 null
     */
    private static DifyDocumentInfo parseDocumentInfo(JsonNode docNode) {
        if (docNode == null || !docNode.has("id")) {
            return null;
        }

        String id = extractString(docNode, "id");
        String name = extractString(docNode, "name");
        Integer position = extractInteger(docNode, "position");
        String indexingStatus = extractString(docNode, "indexing_status");
        Long createdAt = extractLong(docNode, "created_at");
        Integer tokens = extractInteger(docNode, "tokens");
        Integer wordCount = extractInteger(docNode, "word_count");
        String error = extractStringOrNull(docNode, "error");

        return new DifyDocumentInfo(id, name, position, indexingStatus, createdAt, tokens, wordCount, error);
    }

    /**
     * 从 JsonNode 中安全提取字符串值
     *
     * @param node      JsonNode JSON 节点
     * @param fieldName String 字段名
     * @return String 字段值，如果不存在则返回空字符串
     */
    private static String extractString(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return "";
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isTextual() ? fieldNode.asText() : "";
    }

    /**
     * 从 JsonNode 中安全提取字符串值（允许为 null）
     *
     * @param node      JsonNode JSON 节点
     * @param fieldName String 字段名
     * @return String 字段值，如果不存在或为 null 则返回 null
     */
    private static String extractStringOrNull(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode.isNull()) {
            return null;
        }
        return fieldNode.isTextual() ? fieldNode.asText() : null;
    }

    /**
     * 从 JsonNode 中安全提取整数值
     *
     * @param node      JsonNode JSON 节点
     * @param fieldName String 字段名
     * @return Integer 字段值，如果不存在则返回 null
     */
    private static Integer extractInteger(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isNumber() ? fieldNode.asInt() : null;
    }

    /**
     * 从 JsonNode 中安全提取长整数值
     *
     * @param node      JsonNode JSON 节点
     * @param fieldName String 字段名
     * @return Long 字段值，如果不存在则返回 null
     */
    private static Long extractLong(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode.isNumber() ? fieldNode.asLong() : null;
    }

    /**
     * 获取所有文档ID列表（优先使用 documents 数组，否则使用单个 document）
     *
     * @return List<String> 文档ID列表
     */
    public List<String> getAllDocumentIds() {
        if (documents != null && !documents.isEmpty()) {
            return documents.stream()
                    .map(DifyDocumentInfo::getId)
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toList());
        }
        if (document != null && document.getId() != null && !document.getId().trim().isEmpty()) {
            return Collections.singletonList(document.getId());
        }
        return Collections.emptyList();
    }

    /**
     * 判断是否为批量上传（batch 字段非空）
     *
     * @return boolean 是否为批量上传
     */
    public boolean isBatch() {
        return batch != null && !batch.trim().isEmpty();
    }
}
