package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 红头文件解析工作流输入参数配置
 * 类型安全的工作流输入参数定义
 *
 * @param fileUpload DifyFileArrayInput 文件上传参数（数组格式）
 * @author JiaWen.Wu
 * @className RedHeaderFileWorkflowReq
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedHeaderFileWorkflowReq {
    private DifyFileArrayInput fileUpload;

    /**
     * 创建红头文件解析工作流输入参数
     *
     * @param difyFileId String Dify 文件ID
     * @param fileType   String 文件类型（document/image/audio/video）
     * @return RedHeaderFileWorkflowReq 工作流输入参数
     */
    public static RedHeaderFileWorkflowReq of(String difyFileId, String fileType) {
        DifyFileInput fileInput = DifyFileInput.of(difyFileId, fileType);
        return new RedHeaderFileWorkflowReq(new DifyFileArrayInput(Collections.singletonList(fileInput)));
    }

    /**
     * 转换为 Map（用于 DifyWorkflowRequest）
     *
     * @return Map<String, Object> 工作流输入参数 Map
     */
    public Map<String, Object> toInputsMap() {
        Map<String, Object> inputs = new HashMap<String, Object>();
        // 将 DifyFileArrayInput 转换为 JSON 格式
        List<Map<String, Object>> fileArray = fileUpload.getFiles().stream()
                .map(file -> {
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("transfer_method", file.getTransferMethod());
                    fileMap.put("upload_file_id", file.getUploadFileId());
                    fileMap.put("type", file.getType());
                    return fileMap;
                })
                .collect(Collectors.toList());
        inputs.put("fileUpload", fileArray);
        return inputs;
    }
}
