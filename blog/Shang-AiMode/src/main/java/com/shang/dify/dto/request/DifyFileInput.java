package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件输入值（单个文件）
 *
 * @param transferMethod String 传输方式（固定为 "local_file"）
 * @param uploadFileId   String Dify 文件ID
 * @param type           String 文件类型（document/image/audio/video）
 * @author JiaWen.Wu
 * @className DifyFileInput
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyFileInput implements DifyInputValue {
    private String transferMethod;
    private String uploadFileId;
    private String type;

    /**
     * 使用默认传输方式创建文件输入
     *
     * @param uploadFileId String Dify 文件ID
     * @param type         String 文件类型
     * @return DifyFileInput 文件输入
     */
    public static DifyFileInput of(String uploadFileId, String type) {
        return new DifyFileInput("local_file", uploadFileId, type);
    }
}
