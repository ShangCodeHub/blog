package com.shang.dify.controller;

import com.shang.dify.dto.DifyChatbotAppRequest;
import com.shang.dify.dto.DifyChatbotAppResponse;
import com.shang.dify.dto.DifyChatbotMessageRequest;
import com.shang.dify.dto.DifyChatbotModelConfigRequest;
import com.shang.dify.service.DifyApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Dify Chatbot 应用控制器
 * @author
 * @since 2025-11-10
 */
@Slf4j
@RestController
@RequestMapping("/api/dify")
@RequiredArgsConstructor
@Api(tags = "Dify Chatbot API")
public class DifyChatbotController {
    private final DifyApiService difyApiService;
    /**
     * 创建 Chatbot 应用
     */
    @PostMapping("/chatbot/apps")
    @ApiOperation(value = "创建 Dify Chatbot 应用")
    public ResponseEntity<DifyChatbotAppResponse> createChatbotApp(@Valid @RequestBody DifyChatbotAppRequest request) {
        log.info("开始创建 Dify Chatbot 应用: {}", request.getName());
        return difyApiService.createChatbotApp(request);
    }
    /**
     * 更新 Chatbot 应用模型配置
     */
    @PostMapping("/chatbot/apps/{appId}/model-config")
    @ApiOperation(value = "更新 Dify Chatbot 应用模型配置")
    public ResponseEntity<String> updateChatbotModelConfig(
            @PathVariable String appId,
            @Valid @RequestBody DifyChatbotModelConfigRequest config) {
        log.info("更新 Dify Chatbot 应用模型配置, appId={}", appId);
        return difyApiService.updateChatbotModelConfig(appId, config);
    }

    /**
     * Chatbot 阻塞式对话
     */
    @PostMapping("/chatbot/messages")
    @ApiOperation(value = "Chatbot 阻塞式对话")
    public ResponseEntity<String> sendChatbotMessage(@Valid @RequestBody DifyChatbotMessageRequest request) {
        request.setResponseMode("blocking");
        return difyApiService.sendChatbotMessage(request);
    }

    /**
     * Chatbot 流式对话
     */
    @PostMapping(value = "/chatbot/messages/stream")
    @ApiOperation(value = "Chatbot 流式对话（SSE）")
    public ResponseEntity<String> sendChatbotMessageStream(@Valid @RequestBody DifyChatbotMessageRequest request) {
        request.setResponseMode("streaming");
        return difyApiService.sendChatbotMessage(request);
    }
}

