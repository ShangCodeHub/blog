package com.shang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

/**
 * AI 对话请求 DTO
 *
 * @author shihang.shang
 * @since 2025-01-28
 */
@Data
@ApiModel(description = "AI 对话请求参数")
public class ChatRequest {

    @ApiModelProperty(value = "用户消息内容", example = "你好，请介绍一下自己")

    private String message;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId = 1L;
    
    @ApiModelProperty(value = "Chatbot 应用ID", example = "chatbot-app-id-123")

    private String chatbotAppId;
    
    @ApiModelProperty(value = "密钥类型", example = "chatbot", allowableValues = "chatbot,workflow")
    private String keyType = "chatbot";
    
    @ApiModelProperty(value = "会话ID（可选，用于多轮对话）", example = "conversation-123")
    private String conversationId;
}

