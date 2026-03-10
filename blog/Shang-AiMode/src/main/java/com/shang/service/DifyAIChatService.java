package com.shang.service;

import com.shang.dify.dto.DifyChatbotMessageRequest;
import com.shang.dify.service.DifyApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * AI 对话服务类
 * 封装对 Dify Chatbot API 的调用
 *
 * @author shihang.shang
 * @since 2025-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DifyAIChatService {

    private final DifyApiService difyApiService;

    /**
     * 发送 AI 对话消息（阻塞模式）
     * 直接调用 DifyApiService.sendChatbotMessage 方法
     *
     * @param message        用户消息内容
     * @param userId         用户ID
     * @param chatbotAppId   Chatbot 应用ID
     * @param keyType        密钥类型，默认为 "chatbot"
     * @param conversationId 会话ID（可选）
     * @return AI 对话响应
     */
    public ResponseEntity<String> sendMessage(String message, Long userId, String chatbotAppId, 
                                               String keyType, String conversationId) {
        log.info("发送 AI 对话消息: userId={}, chatbotAppId={}, message={}, conversationId={}", 
                userId, chatbotAppId, message, conversationId);

        // 构建 Chatbot 请求
        DifyChatbotMessageRequest request = new DifyChatbotMessageRequest();
        request.setUserId(userId);
        request.setResourceId(chatbotAppId);
        request.setKeyType(keyType != null ? keyType : "chatbot");
        request.setQuery(message);
        request.setResponseMode("blocking");
        request.setUser("user_" + userId);
        
        // 设置会话ID（如果提供）
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            request.setConversationId(conversationId);
        }

        // 直接调用 DifyApiService.sendChatbotMessage 方法
        ResponseEntity<String> response = difyApiService.sendChatbotMessage(request);

        log.info("AI 对话响应完成: userId={}, chatbotAppId={}, status={}", 
                userId, chatbotAppId, response.getStatusCode());
        
        return response;
    }
    /**
     * 发送 AI 对话消息（流式响应）
     * 直接返回 InputStream，由调用者负责读取和关闭
     *
     * @param message        用户消息内容
     * @param userId         用户ID
     * @param chatbotAppId   Chatbot 应用ID
     * @param keyType        密钥类型，默认为 "chatbot"
     * @param conversationId 会话ID（可选）
     * @return InputStream 响应流，调用者需要负责关闭
     */
    public InputStream sendMessageStream(String message, Long userId, String chatbotAppId,
                                         String keyType, String conversationId) {
        log.info("发送 AI 对话消息（流式）: userId={}, chatbotAppId={}, message={}, conversationId={}", 
                userId, chatbotAppId, message, conversationId);

        // 构建 Chatbot 请求
        DifyChatbotMessageRequest request = new DifyChatbotMessageRequest();
        request.setUserId(userId);
        request.setResourceId("workflow_001");
        request.setKeyType("workflow");
        request.setQuery(message);
        request.setResponseMode("streaming");
        request.setUser("user_" + 1);
        
        // 设置会话ID（如果提供）
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            request.setConversationId(conversationId);
        }

        // 返回 ResponseBody
        return difyApiService.sendChatbotMessageStream(request);
    }   
    /**
     * 上传文章到 Dify 知识库（通过文本方式）
     * 
     * @param articleTitle   文章标题
     * @param articleContent 文章内容
     * @param datasetId      数据集ID（知识库ID）
     * @param userId         用户ID
     * @param resourceId     资源ID（用于查找 API Key）
     * @param keyType        密钥类型（dataset/file）
     * @return 响应结果
     */
    public ResponseEntity<String> uploadArticleToKnowledgeBaseByText(String articleTitle, String articleContent,
                                                                     String datasetId, Long userId, String resourceId, String keyType) {
        log.info("上传文章到 Dify 知识库: title={}, contentLength={}, datasetId={}, userId={}, resourceId={}, keyType={}",
                articleTitle, articleContent != null ? articleContent.length() : 0, datasetId, userId, resourceId, keyType);
        
        // 调用 DifyApiService 通过文本创建文档
        return difyApiService.createDocumentByText(datasetId, articleTitle, articleContent, userId, resourceId, keyType);
    }
}