package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天工作流输入参数配置
 * 类型安全的工作流输入参数定义
 *
 * @param file DifyFileArrayInput 文件上传参数（数组格式）
 * @author JiaWen.Wu
 * @className ChatWorkflowReq
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatWorkflowReq {
    private DifyFileArrayInput file;

    /**
     * 创建聊天工作流输入参数（单个文件）
     *
     * @param difyFileId String Dify 文件ID
     * @param fileType   String 文件类型（document/image/audio/video）
     * @return ChatWorkflowReq 工作流输入参数
     */
    public static ChatWorkflowReq of(String difyFileId, String fileType) {
        DifyFileInput fileInput = DifyFileInput.of(difyFileId, fileType);
        return new ChatWorkflowReq(new DifyFileArrayInput(Collections.singletonList(fileInput)));
    }

    /**
     * 创建聊天工作流输入参数（多个文件）
     *
     * @param difyFileIds List<String> Dify 文件ID列表
     * @param fileType    String 文件类型（document/image/audio/video）
     * @return ChatWorkflowReq 工作流输入参数
     */
    public static ChatWorkflowReq of(List<String> difyFileIds, String fileType) {
        List<DifyFileInput> fileInputs = difyFileIds.stream()
                .map(fileId -> DifyFileInput.of(fileId, fileType))
                .collect(Collectors.toList());
        return new ChatWorkflowReq(new DifyFileArrayInput(fileInputs));
    }

    /**
     * 转换为 Map（用于 DifyWorkflowRequest）
     *
     * @return Map<String, Object> 工作流输入参数 Map
     */
    public Map<String, Object> toInputsMap() {
        Map<String, Object> inputs = new HashMap<String, Object>();
        // 将 DifyFileArrayInput 转换为 JSON 格式
        List<Map<String, Object>> fileArray = file.getFiles().stream()
                .map(f -> {
                    Map<String, Object> fileMap = new HashMap<String, Object>();
                    fileMap.put("transfer_method", f.getTransferMethod());
                    fileMap.put("upload_file_id", f.getUploadFileId());
                    fileMap.put("type", f.getType());
                    return fileMap;
                })
                .collect(Collectors.toList());
        inputs.put("file", fileArray);
        return inputs;
    }
}
