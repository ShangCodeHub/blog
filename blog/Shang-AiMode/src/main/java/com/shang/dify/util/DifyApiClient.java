package com.shang.dify.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shang.dify.config.DifyConfig;
import com.shang.dify.dto.DifyChatbotMessageRequest;
import com.shang.dify.service.DifyApiKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;
/**
 * Dify API 客户端工具类
 * 封装 HTTP 请求和 Authorization 认证
 * 
 * @author shihang.shang
 * @since
 */
@Slf4j
@Component
public class DifyApiClient {
    private final DifyConfig difyConfig;
    private final RestTemplate restTemplate;
    private final DifyApiKeyService difyApiKeyService;
    private final ObjectMapper objectMapper;
    public DifyApiClient(DifyConfig difyConfig, RestTemplate restTemplate, 
                        DifyApiKeyService difyApiKeyService, ObjectMapper objectMapper) {
        this.difyConfig = difyConfig;
        this.restTemplate = restTemplate;
        this.difyApiKeyService = difyApiKeyService;
        this.objectMapper = objectMapper;
    }
    

    /**
     * 统一请求方法（使用动态密钥）
     * 
     * @param method     请求类型 (GET, POST, PUT, DELETE)
     * @param path       请求路径
     * @param body       请求体 (POST/PUT 时使用，会被序列化为 JSON)
     * @param params     查询参数 (GET 时使用，会拼接到 URL)
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @param key        URL 类型（0=baseUrl, 1=privateUrl）
     * @return 响应结果
     */
    public ResponseEntity<String> request(String method, String path, Object body, Map<String, Object> params,
            Long userId, String resourceId, String keyType, int key) {
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        String url = buildUrl(path, params, key);
        HttpEntity<?> entity = createHttpEntityWithDynamicKey(body, userId, resourceId, keyType, key);
        log.debug("Dify {} 请求: {}, userId={}, resourceId={}, keyType={}, hasBody={}",
                method, url, userId, resourceId, keyType, body != null);

        // 使用 URI.create() 避免 RestTemplate 将 URL 中的 {} 当作 URI 模板变量处理
        try {
            URI uri = URI.create(url);
            ResponseEntity<String> response = restTemplate.exchange(uri, httpMethod, entity, String.class);
            validateResponse(url, response);
            return response;
        } catch (IllegalArgumentException e) {
            log.error("URL 格式错误: {}, 错误: {}", url, e.getMessage());
            throw new RuntimeException("URL 格式错误: " + url, e);
        }
    }
    /**
     * GET 请求方法（无请求体，无查询参数）
     * 
     * @param method     请求类型（通常是 GET）
     * @param path       请求路径
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果
     */
    public ResponseEntity<String> request(String method, String path, Long userId, String resourceId, String keyType) {
        return request(method, path, null, null, userId, resourceId, keyType, 0);
    }
    /**
     * POST 请求方法（带请求体，body 会被序列化为 JSON）
     * 
     * @param method     请求类型（通常是 POST）
     * @param path       请求路径
     * @param body       请求体（会被序列化为 JSON 字符串）
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果
     */
    public ResponseEntity<String> request(String method, String path, Object body, Long userId, String resourceId,
            String keyType) {
        return request(method, path, body, null, userId, resourceId, keyType, 0);
    }

    /**
     * POST 请求方法（带请求体，body 会被序列化为 JSON，支持指定 URL 类型）
     * 
     * @param method     请求类型（通常是 POST）
     * @param path       请求路径
     * @param body       请求体（会被序列化为 JSON 字符串）
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @param key        URL 类型（0=baseUrl, 1=privateUrl）
     * @return 响应结果
     */
    public ResponseEntity<String> request(String method, String path, Object body, Long userId, String resourceId,
            String keyType, int key) {
        return request(method, path, body, null, userId, resourceId, keyType, key);
    }
    /**
     * GET 请求方法（带查询参数，params 会拼接到 URL）
     * 
     * @param method     请求类型（通常是 GET）
     * @param path       请求路径
     * @param params     查询参数（会拼接到 URL 的查询字符串）
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果
     */
    public ResponseEntity<String> request(String method, String path, Map<String, Object> params, Long userId,
            String resourceId, String keyType) {
        return request(method, path, null, params, userId, resourceId, keyType, 0);
    }

    /**
     * 文件上传请求方法（使用动态密钥）
     * 
     * @param method     请求类型 (POST)
     * @param path       请求路径
     * @param file       上传的文件
     * @param data       其他表单数据
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果
     */
    public ResponseEntity<String> uploadFile(String method, String path, MultipartFile file, Map<String, Object> data,
            Long userId, String resourceId, String keyType) {
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        String url = difyConfig.getBaseUrl() + path;
        HttpEntity<?> entity = createFileUploadEntityWithDynamicKey(file, data, userId, resourceId, keyType);

        log.debug("Dify {} 文件上传请求: {}, userId={}, resourceId={}, keyType={}",
                method, url, userId, resourceId, keyType);
        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);
        validateResponse(url, response);
        return response;
    }

    /**
     * 批量文件上传请求方法（使用动态密钥）
     * 支持一次上传多个文件（最多20个，系统限制为10个）
     * 
     * @param method     请求类型 (POST)
     * @param path       请求路径
     * @param files      上传的文件列表
     * @param data       其他表单数据
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果
     */
    public ResponseEntity<String> uploadFiles(String method, String path, MultipartFile[] files,
            Map<String, Object> data,
            Long userId, String resourceId, String keyType) {
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        String url = difyConfig.getBaseUrl() + path;
        HttpEntity<?> entity = createBatchFileUploadEntityWithDynamicKey(files, data, userId, resourceId, keyType);

        log.debug("Dify {} 批量文件上传请求: {}, fileCount={}, userId={}, resourceId={}, keyType={}",
                method, url, files != null ? files.length : 0, userId, resourceId, keyType);
        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);
        validateResponse(url, response);
        return response;
    }

    /**
     * 构建完整URL
     */
    private String buildUrl(String path, Map<String, Object> params, int Urlkey) {
        String url = "";
        if (Urlkey == 0)
            url = difyConfig.getBaseUrl() + path;
        if (Urlkey == 1)
            url = difyConfig.getPrivateUrl() + path;

        if (params != null && !params.isEmpty()) {
            StringBuilder queryString = new StringBuilder("?");
            params.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));
            url += queryString.toString().replaceAll("&$", "");
        }

        return url;
    }

    /**
     * 创建带动态密钥的 HTTP 实体
     * 
     * @param body       请求体（如果不为 null，会被序列化为 JSON 字符串）
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @param IsKey      URL 类型（0=baseUrl, 1=privateUrl）
     * @return HTTP 实体
     */
    private HttpEntity<?> createHttpEntityWithDynamicKey(Object body, Long userId, String resourceId, String keyType,
            int IsKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        // 如果提供了 userId、resourceId、keyType，则使用动态密钥（无论是 baseUrl 还是 privateUrl）
        if (userId != null && resourceId != null && keyType != null) {
            // 动态获取API密钥
            String apiKey = difyApiKeyService.getApiKey(userId, resourceId, keyType);
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("API密钥为空: userId={}, resourceId={}, keyType={}", userId, resourceId, keyType);
                throw new RuntimeException(String.format("API密钥为空: userId=%s, resourceId=%s, keyType=%s", 
                    userId, resourceId, keyType));
            }
            headers.set("Authorization", "Bearer " + apiKey);
            log.debug("设置Authorization header: userId={}, resourceId={}, keyType={}, apiKeyLength={}", 
                userId, resourceId, keyType, apiKey.length());
        } else {
            log.warn("缺少认证参数: userId={}, resourceId={}, keyType={}", userId, resourceId, keyType);
        }

        // 如果 body 不为 null，将其序列化为 JSON 字符串，确保作为请求体发送（而不是查询参数）
        if (body != null) {
            try {
                String jsonBody = objectMapper.writeValueAsString(body);
                return new HttpEntity<>(jsonBody, headers);
            } catch (Exception e) {
                log.error("序列化请求体失败: {}", e.getMessage(), e);
                throw new RuntimeException("序列化请求体失败: " + e.getMessage(), e);
            }
        }

        return new HttpEntity<>(headers);
    }

    /**
     * 创建带动态密钥的文件上传 HTTP 实体
     */
    private HttpEntity<?> createFileUploadEntityWithDynamicKey(MultipartFile file, Map<String, Object> data,
            long userId, String resourceId, String keyType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        // 动态获取API密钥
        String apiKey = difyApiKeyService.getApiKey(userId, resourceId, keyType);
        headers.set("Authorization", "Bearer " + apiKey);

        // 构建multipart数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加文件
        if (file != null && !file.isEmpty()) {
            body.add("file", file.getResource());
        }

        // 添加其他数据
        if (data != null) {
            data.forEach((key, value) -> {
                if (value != null) {
                    body.add(key, value.toString());
                }
            });
        }

        return new HttpEntity<>(body, headers);
    }

    /**
     * 创建带动态密钥的批量文件上传 HTTP 实体
     * 支持多个文件上传（每个文件使用相同的 field name "file"）
     * 根据 Dify 官方文档，批量上传时每个文件都使用 "file" 作为字段名
     */
    private HttpEntity<?> createBatchFileUploadEntityWithDynamicKey(MultipartFile[] files, Map<String, Object> data,
            long userId, String resourceId, String keyType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        // 动态获取API密钥
        String apiKey = difyApiKeyService.getApiKey(userId, resourceId, keyType);
        headers.set("Authorization", "Bearer " + apiKey);

        // 构建multipart数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加多个文件（每个文件都使用 "file" 作为 field name，符合 Dify API 规范）
        // 这是批量上传的关键：多个文件使用相同的字段名 "file"
        if (files != null && files.length > 0) {
            int fileCount = 0;
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    body.add("file", file.getResource());
                    fileCount++;
                    log.debug("添加文件到批量上传请求: fileName={}, fileCount={}/{}",
                            file.getOriginalFilename(), fileCount, files.length);
                }
            }
            log.info("批量上传请求构建完成: totalFileCount={}, actualFileCount={}", files.length, fileCount);
        }

        // 添加其他数据（所有文件共享同一个 data 配置）
        if (data != null) {
            data.forEach((key, value) -> {
                if (value != null) {
                    body.add(key, value.toString());
                }
            });
            log.debug("添加表单数据到批量上传请求: dataKeys={}", data.keySet());
        }

        return new HttpEntity<>(body, headers);
    }

    /**
     * 流式请求方法（使用 HttpURLConnection 处理 text/event-stream 响应，支持中文）
     * 直接返回 InputStream，由调用者负责读取和关闭
     * 
     * @param method     请求类型 (POST)
     * @param path       请求路径
     * @param request    请求体（DifyChatbotMessageRequest 类型）
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return InputStream 响应流，调用者需要负责关闭
     * @throws RuntimeException 如果请求失败
     */
    public java.io.InputStream requestStreamInputStream(String method, String path, DifyChatbotMessageRequest request,
            Long userId, String resourceId, String keyType) {
        String url = buildUrl(path, null, 0);
        HttpEntity<?> entity = createHttpEntityWithDynamicKey(request, userId, resourceId, keyType, 0);
        
        log.info("Dify {} 流式请求（HttpURLConnection）: {}, userId={}, resourceId={}, keyType={}, hasBody={}",
                method, url, userId, resourceId, keyType, request != null);

        HttpURLConnection connection = null;
        
        try {
            // 确保 query 字段在 inputs 中
            if (request != null) {
                String queryValue = request.getQuery();
                
                if (queryValue == null) {
                    log.warn("query字段为null，设置为空字符串以确保字段存在");
                    queryValue = "";
                    request.setQuery(queryValue);
                }
                
                // 确保 query 也在 inputs 中（workflow 类型需要）
                if (request.getInputs() == null) {
                    request.setInputs(new java.util.HashMap<>());
                }
                if (!request.getInputs().containsKey("query")) {
                    request.getInputs().put("query", queryValue);
                    log.info("将query字段添加到inputs中: query={}", queryValue);
                }
            }
            
            // 构建请求体 JSON 字符串（直接序列化 request，不包含继承的字段）
            String jsonBody;
            if (request != null) {
                jsonBody = objectMapper.writeValueAsString(request);
                log.info("序列化后的请求体: {}", jsonBody);
            } else {
                jsonBody = "{}";
                log.warn("请求体为空，使用空JSON对象");
            }
            
            // 验证请求体是否包含必要字段
            if (jsonBody != null && !jsonBody.contains("\"query\"")) {
                log.error("请求体缺少 query 字段！jsonBody={}", jsonBody);
                throw new RuntimeException(
                        "请求体缺少必需的 query 字段。请确保 DifyChatbotMessageRequest.query 不为 null。jsonBody=" + jsonBody);
            }
            
            // 创建 HTTP 连接
            URI uri = new URI(url);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(method.toUpperCase());
            connection.setDoOutput(true);
            connection.setDoInput(true);    
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(10000); // 10秒读取超时（首次响应）
            // 设置请求头
            HttpHeaders headers = entity.getHeaders();
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values != null && !values.isEmpty()) {
                        connection.setRequestProperty(name, values.get(0));
                    }
                }
            }
            // 设置请求头，确保使用 UTF-8 编码
            connection.setRequestProperty("Content-Type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8");
            connection.setRequestProperty("Accept", org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE + "; charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Accept-Encoding", "identity"); // 禁用压缩，避免编码问题
            
            // 发送请求体
            if (jsonBody != null && !jsonBody.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    os.flush(); // 确保数据完全发送
                }
            }

            // 获取响应码
            int responseCode = connection.getResponseCode();
            if (responseCode >= 400) {
                // 读取错误响应
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line).append("\n");
                    }
                    log.error("流式请求失败: url={}, status={}, error={}", url, responseCode, errorResponse.toString());
                    throw new RuntimeException(String.format("流式请求失败: status=%d, error=%s", responseCode, errorResponse.toString()));
                }
            }
            
            // 检查响应头 Content-Type
            String contentTypeHeader = connection.getContentType();
            if (contentTypeHeader != null) {
                log.info("响应 Content-Type: {}", contentTypeHeader);
                // 如果响应头中没有指定 charset，记录警告
                if (!contentTypeHeader.toLowerCase().contains("charset")) {
                    log.warn("响应头 Content-Type 未指定 charset，将强制使用 UTF-8 解码");
                }
            }
            
            // 直接返回响应流的 InputStream，这是真正的原始字节流
            // 注意：HttpURLConnection 返回的 InputStream 是原始字节流，不涉及字符编码
            // 调用者必须使用 UTF-8 的 InputStreamReader 来解码，否则会出现乱码
            log.info("✅ 成功获取流式响应 InputStream，确保使用 UTF-8 解码");
            return connection.getInputStream();
            
        } catch (Exception e) {
            log.error("Dify 流式请求异常: url={}, err={}", url, e.getMessage(), e);
            if (connection != null) {
                connection.disconnect();
            }
            throw new RuntimeException("Dify 流式请求异常: " + url + ", " + e.getMessage(), e);
        }
    }

    /**
     * 流式请求方法（使用 HttpURLConnection 处理 text/event-stream 响应）
     * 注意：此方法会等待完整响应后才返回，不是真正的实时流式
     * 
     * @param method     请求类型 (POST)
     * @param path       请求路径
     * @param body       请求体
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果
     */
    public ResponseEntity<String> requestStream(String method, String path, Object body,
            Long userId, String resourceId, String keyType) {
        String url = buildUrl(path, null, 0);
        HttpEntity<?> entity = createHttpEntityWithDynamicKey(body, userId, resourceId, keyType, 0);

        log.debug(String.format("Dify %s 流式请求: %s, userId=%s, resourceId=%s, keyType=%s, hasBody=%s",
                method, url, userId, resourceId, keyType, body != null));

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            // 构建请求体
            String jsonBody;
            if (entity.getBody() instanceof String) {
                jsonBody = (String) entity.getBody();
            } else if (entity.getBody() != null) {
                jsonBody = objectMapper.writeValueAsString(entity.getBody());
            } else {
                jsonBody = "{}";
            }
            
            // 创建 HTTP 连接
            URI uri = new URI(url);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(method.toUpperCase());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(10000); // 10秒读取超时（首次响应）
            
            // 设置请求头
            HttpHeaders headers = entity.getHeaders();
            if (headers != null) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values != null && !values.isEmpty()) {
                        connection.setRequestProperty(name, values.get(0));
                    }
                }
            }
            connection.setRequestProperty("Content-Type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
            connection.setRequestProperty("Accept", org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE);
            
            // 发送请求体
            if (jsonBody != null && !jsonBody.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            
            // 检查响应码
            int responseCode = connection.getResponseCode();
            if (responseCode >= 400) {
                // 读取错误响应
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line).append("\n");
                    }
                    log.error("流式请求失败: url={}, status={}, error={}", url, responseCode, errorResponse.toString());
                    return ResponseEntity.status(responseCode).body(errorResponse.toString());
                }
            }
            
            // 读取流式响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder responseBodyBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBodyBuilder.append(line).append("\n");
            }
            
            String responseBody = responseBodyBuilder.toString();
            log.debug(String.format("流式响应读取成功: url=%s, bodyLength=%d", url, responseBody.length()));
            
            // 构建 ResponseEntity<String>
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(org.springframework.http.MediaType.TEXT_EVENT_STREAM);
            ResponseEntity<String> httpResponse = new ResponseEntity<>(
                    responseBody,
                    responseHeaders,
                    HttpStatus.OK);

            validateResponse(url, httpResponse);
            return httpResponse;

        } catch (Exception e) {
            log.error(String.format("Dify 流式请求异常: url=%s, err=%s", url, e.getMessage()), e);
            throw new RuntimeException("Dify 流式请求异常: " + url, e);
        } finally {
            // 关闭资源
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.warn("关闭流式响应读取器失败", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    /**
     * 查询批次索引状态（用于批量上传后获取所有文件的详细信息）
     * 根据 Dify 官方文档：GET /datasets/{dataset_id}/documents/{batch}/indexing-status
     * 
     * @param datasetId  数据集ID
     * @param batch      批次ID
     * @param userId     用户ID
     * @param resourceId 资源ID
     * @param keyType    密钥类型
     * @return 响应结果（包含所有文件的详细信息）
     */
    public ResponseEntity<String> getBatchIndexingStatus(String datasetId, String batch, Long userId,
            String resourceId, String keyType) {
        try {
            // 修正路径：应该是 /documents/{batch}/indexing-status 而不是
            // /batch/{batch}/indexing-status
            String path = "/datasets/" + datasetId + "/documents/" + batch + "/indexing-status";
            log.debug("查询批次索引状态: datasetId={}, batch={}, userId={}, resourceId={}, keyType={}",
                    datasetId, batch, userId, resourceId, keyType);
            return request("GET", path, userId, resourceId, keyType);
        } catch (Exception e) {
            log.error("查询批次索引状态失败: datasetId={}, batch={}, error={}", datasetId, batch, e.getMessage(), e);
            throw new RuntimeException("查询批次索引状态失败: " + e.getMessage(), e);
        }
    }
    /**
     * 验证响应状态码
     *
     * @param url      请求URL
     * @param response HTTP响应
     */
    private void validateResponse(String url, ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Dify 请求失败，url: " + url
                    + ", 状态码: " + response.getStatusCode()
                    + ", 响应体: " + response.getBody());
        }
    }
}