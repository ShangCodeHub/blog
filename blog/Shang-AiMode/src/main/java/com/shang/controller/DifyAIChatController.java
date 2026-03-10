package com.shang.controller;

import com.shang.dto.ChatRequest;
import com.shang.service.DifyAIChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * AI 对话控制器
 * 提供简化的 AI 对话 REST API 接口
 * 内部调用 Dify Chatbot API
 *
 * @author shihang.shang
 * @since 2025-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/dify/chat")
@RequiredArgsConstructor
@Api(tags = "AI 对话 API")
public class DifyAIChatController {
    
    private final DifyAIChatService aiChatService;
    
    /**
     * 发送 AI 对话消息（阻塞模式）
     * 
     * 请求示例：
     * {
     *   "message": "你好，请介绍一下自己",
     *   "userId": 1,
     *   "chatbotAppId": "chatbot-app-id-123",d
     *   "keyType": "chatbot",
     *   "conversationId": "conversation-123"
     * }
     *
     * @param request AI 对话请求
     * @return AI 对话响应
     */
    @PostMapping("/send")
    @ApiOperation(value = "发送 AI 对话消息", notes = "发送消息给 AI Chatbot，等待完整响应后返回（阻塞模式）")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("接收 AI 对话请求: userId={}, chatbotAppId={}, message={}, conversationId={}", 
                request.getUserId(), request.getChatbotAppId(), request.getMessage(), request.getConversationId());
        return aiChatService.sendMessage(
                request.getMessage(),
                request.getUserId(),
                request.getChatbotAppId(),
                request.getKeyType(),
                request.getConversationId()
        );
    }
    
    /**
     * 发送 AI 对话消息（流式模式 - SSE）
     * 使用 Server-Sent Events (SSE) 实时返回流式数据
     * <p>
     * 请求示例：
     * {
     * "message": "你好，请介绍一下自己",
     * "userId": 1,
     * "chatbotAppId": "chatbot-app-id-123",
     * "keyType": "chatbot",
     * "conversationId": "conversation-123"
     * }
     *
     * @param request AI 对话请求
     * @return SSE 流式响应
     */
    @PostMapping(value = "/stream", 
                 consumes = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8",
                 produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    @ApiOperation(value = "发送 AI 对话消息（流式）", notes = "发送消息给 AI Chatbot，使用 SSE 流式传输")
    public SseEmitter sendMessageStream(@Valid @RequestBody ChatRequest request) {
        // 打印接收到的原始消息（用于调试编码问题）
        log.info("接收 AI 对话流式请求: userId={}, chatbotAppId={}, message={}, conversationId={}", 
                request.getUserId(), request.getChatbotAppId(), request.getMessage(), request.getConversationId());
        
        // 检查消息中的中文字符
        if (request.getMessage() != null) {
            String message = request.getMessage();
            boolean hasChinese = message.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FFF);
            if (hasChinese) {
                log.info("✅ 接收到的消息包含中文字符，长度={}, 内容={}", message.length(), message);
            } else {
                log.warn("⚠️ 接收到的消息不包含中文字符，可能编码有问题: message={}, length={}", message, message.length());
                // 打印字符编码
                StringBuilder charCodes = new StringBuilder();
                for (int i = 0; i < Math.min(message.length(), 50); i++) {
                    charCodes.append((int)message.charAt(i)).append(" ");
                }
                log.warn("消息的字符编码: {}", charCodes.toString());
            }
        }
        
        // 创建 SSE Emitter，设置超时时间为 10 分钟
        SseEmitter emitter = new SseEmitter(600000L);
        
        // 在后台线程中异步处理流式响应，避免阻塞
        new Thread(() -> {
            InputStream inputStream = null;
            try {
                // 获取原始 InputStream（10秒超时）
                inputStream = aiChatService.sendMessageStream(
                        request.getMessage(),
                        request.getUserId(),
                        request.getChatbotAppId(),
                        request.getKeyType(),
                        request.getConversationId());
                
                if (inputStream == null) {
                    log.error("获取到的 InputStream 为 null");
                    emitter.send(SseEmitter.event()
                            .data("{\"event\":\"error\",\"message\":\"无法获取流式响应，请稍后重试\"}")
                            .name("error"));
                    emitter.complete();
                    return;
                }
                
                // 使用 UTF-8 编码读取流，使用字节缓冲区避免多字节字符截断问题
                // 使用较大的缓冲区（8KB）确保能完整读取多字节字符
                byte[] buffer = new byte[8192];
                StringBuilder lineBuffer = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // 打印原始字节（用于调试）
                    log.debug("收到原始字节数: {}, 前100字节: {}", bytesRead, 
                            bytesToHexString(buffer, Math.min(100, bytesRead)));
                    
                    // 将字节转换为 UTF-8 字符串
                    String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    
                    // 打印转换后的完整字符串（不截断）
                    log.info("收到数据块 (UTF-8解码后): length={}, 完整内容:\n{}", 
                            chunk.length(), chunk);
                    
                    // 检查是否包含中文字符
                    boolean hasChinese = chunk.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FFF);
                    if (hasChinese) {
                        log.info("✅ 检测到中文字符，UTF-8解码正常");
                        // 提取并打印所有中文字符片段
                        StringBuilder chineseParts = new StringBuilder();
                        boolean inChinese = false;
                        int start = -1;
                        for (int i = 0; i < chunk.length(); i++) {
                            char c = chunk.charAt(i);
                            if (c >= 0x4E00 && c <= 0x9FFF) {
                                if (!inChinese) {
                                    inChinese = true;
                                    start = i;
                                }
                            } else {
                                if (inChinese && start >= 0) {
                                    chineseParts.append(chunk.substring(start, i)).append(" | ");
                                    inChinese = false;
                                    start = -1;
                                }
                            }
                        }
                        if (inChinese && start >= 0) {
                            chineseParts.append(chunk.substring(start));
                        }
                        if (chineseParts.length() > 0) {
                            log.info("提取的中文字符片段: {}", chineseParts.toString());
                        }
                    }
                    
                    lineBuffer.append(chunk);
                    
                    // 按行分割并发送
                    int lineStart = 0;
                    while (true) {
                        int lineEnd = lineBuffer.indexOf("\n", lineStart);
                        if (lineEnd == -1) {
                            // 没有找到换行符，保留剩余内容到下次读取
                            if (lineStart > 0) {
                                lineBuffer.delete(0, lineStart);
                            }
                            break;
                        }
                        
                        // 提取一行（包含换行符）
                        String line = lineBuffer.substring(lineStart, lineEnd + 1);
                        lineStart = lineEnd + 1;
                        
                        // 打印即将发送的完整行（不截断）
                        log.info("准备发送 SSE 行 (完整内容):\n{}", line);
                        
                        // 发送 SSE 事件
                        // 注意：SseEmitter.data() 会自动添加 "data: " 前缀，所以不需要在字符串中包含
                        // 如果行以 "data:" 开头，需要移除这个前缀
                        String dataToSend = line;
                        if (line.startsWith("data:")) {
                            // 移除 "data:" 前缀，SseEmitter 会自动添加
                            dataToSend = line.substring(5).trim();
                            // 如果移除前缀后还有内容，发送
                            if (!dataToSend.isEmpty()) {
                                emitter.send(SseEmitter.event()
                                        .data(dataToSend)
                                        .name("message"));
                                log.info("✅ 已发送 SSE 事件 (移除data:前缀后)，内容:\n{}", dataToSend);
                            }
                        } else if (line.startsWith("event:")) {
                            // event: 行需要特殊处理，提取事件类型
                            String eventType = line.substring(6).trim();
                            emitter.send(SseEmitter.event()
                                    .name(eventType)
                                    .data(""));
                            log.info("✅ 已发送 SSE 事件类型: {}", eventType);
                        } else if (line.trim().isEmpty()) {
                            // 空行表示 SSE 事件结束，跳过
                            continue;
                        } else {
                            // 其他行直接发送（不包含 data: 前缀）
                            emitter.send(SseEmitter.event()
                                    .data(dataToSend)
                                    .name("message"));
                            log.info("✅ 已发送 SSE 事件 (其他)，完整内容:\n{}", dataToSend);
                        }
                    }
                }
                
                // 处理最后一行（可能没有换行符）
                if (lineBuffer.length() > 0) {
                    String remaining = lineBuffer.toString();
                    if (!remaining.trim().isEmpty()) {
                        log.info("发送最后一行数据 (完整内容):\n{}", remaining);
                        // 确保使用 UTF-8 编码
                        byte[] remainingBytes = remaining.getBytes(StandardCharsets.UTF_8);
                        String remainingUtf8 = new String(remainingBytes, StandardCharsets.UTF_8);
                        emitter.send(SseEmitter.event()
                                .data(remainingUtf8)
                                .name("message"));
                    }
                }
                
                log.info("✅ SSE 流式响应完成");
                emitter.complete();
                
            } catch (java.net.SocketTimeoutException e) {
                log.error("流式请求超时（10秒）: {}", e.getMessage(), e);
                try {
                    // 发送超时错误事件
                    String errorMsg = "{\"event\":\"error\",\"message\":\"请求超时，请稍后重试\"}";
                    emitter.send(SseEmitter.event()
                            .data(errorMsg)
                            .name("error"));
                } catch (Exception sendEx) {
                    log.error("发送错误事件失败", sendEx);
                }
                emitter.complete();
            } catch (java.net.ConnectException e) {
                log.error("流式请求连接失败: {}", e.getMessage(), e);
                try {
                    // 发送连接错误事件
                    String errorMsg = "{\"event\":\"error\",\"message\":\"连接失败，请稍后重试\"}";
                    emitter.send(SseEmitter.event()
                            .data(errorMsg)
                            .name("error"));
                } catch (Exception sendEx) {
                    log.error("发送错误事件失败", sendEx);
                }
                emitter.complete();
            } catch (java.io.IOException e) {
                log.error("流式响应转发时发生网络错误: {}", e.getMessage(), e);
                try {
                    // 发送错误事件
                    String errorMsg = String.format("{\"event\":\"error\",\"message\":\"%s，请稍后重试\"}", 
                        e.getMessage().replace("\"", "\\\"").replace("\n", " "));
                    emitter.send(SseEmitter.event()
                            .data(errorMsg)
                            .name("error"));
                } catch (Exception sendEx) {
                    log.error("发送错误事件失败", sendEx);
                }
                emitter.complete();
            } catch (Exception e) {
                log.error("转发流式响应时发生错误", e);
                try {
                    // 发送错误事件
                    String errorMsg = String.format("{\"event\":\"error\",\"message\":\"%s，请稍后重试\"}", 
                        e.getMessage().replace("\"", "\\\"").replace("\n", " "));
                    emitter.send(SseEmitter.event()
                            .data(errorMsg)
                            .name("error"));
                } catch (Exception sendEx) {
                    log.error("发送错误事件失败", sendEx);
                }
                emitter.complete();
            } finally {
                // 关闭资源
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        log.warn("关闭 InputStream 时发生错误", e);
                    }
                }
            }
        }).start();
        
        return emitter;
    }
    
    /**
     * 将字节数组转换为十六进制字符串（用于调试）
     */
    private String bytesToHexString(byte[] bytes, int length) {
        if (bytes == null || length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int actualLength = Math.min(length, bytes.length);
        for (int i = 0; i < actualLength; i++) {
            sb.append(String.format("%02X ", bytes[i]));
            if ((i + 1) % 16 == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * 发送 AI 对话消息（简化接口，使用 GET 方法）
     * 
     * 请求示例：
     * GET /api/chat/send?message=你好&userId=1&chatbotAppId=chatbot-app-id-123
     *
     * @param message        用户消息内容
     * @param userId         用户ID
     * @param chatbotAppId   Chatbot 应用ID
     * @param keyType        密钥类型（可选，默认为 "chatbot"）
     * @param conversationId 会话ID（可选）
     * @return AI 对话响应
     */
    @GetMapping("/send")
    @ApiOperation(value = "发送 AI 对话消息（GET）", notes = "使用 GET 方法发送消息给 AI Chatbot（简化接口）")
    public ResponseEntity<String> sendMessageGet(
            @RequestParam("message") String message,
            @RequestParam(value = "userId", required = false, defaultValue = "1") Long userId,
            @RequestParam("chatbotAppId") String chatbotAppId,
            @RequestParam(value = "keyType", required = false, defaultValue = "chatbot") String keyType,
            @RequestParam(value = "conversationId", required = false) String conversationId) {
        log.info("接收 AI 对话请求（GET）: userId={}, chatbotAppId={}, message={}, conversationId={}", 
                userId, chatbotAppId, message, conversationId);
        return aiChatService.sendMessage(message, userId, chatbotAppId, keyType, conversationId);
    }
}

