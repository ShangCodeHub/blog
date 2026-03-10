package com.shang.dify.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Dify Chatbot 对话请求
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DifyChatbotMessageRequest {
    /**
     * 用户ID（不序列化到请求体，仅用于内部获取API Key）
     */
    @JsonIgnore
    private Long userId;
    /**
     * 资源ID（不序列化到请求体，仅用于内部获取API Key）
     */
    @JsonIgnore
    private String resourceId;
    /**
     * 密钥类型（不序列化到请求体，仅用于内部获取API Key）
     */
    @JsonIgnore
    private String keyType;
    /**
     * 输入参数
     */
    private Map<String, Object> inputs = new HashMap<>();
    /**
     * 用户问题
     */
    private String query;
    /**
     * 响应模式：blocking 或 streaming
     */
    @JsonProperty("response_mode")
    private String responseMode = "blocking";
    /**
     * 会话ID
     */
    @JsonProperty("conversation_id")
    private String conversationId = "";
    /**
     * 用户标识
     */
    private String user;
    /**
     * 附件
     */
    private List<ChatFile> files = new ArrayList<>();
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatFile {
        private String type;
        @JsonProperty("transfer_method")
        private String transferMethod;
        private String url;
    }
}