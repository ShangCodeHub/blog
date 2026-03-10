package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件同步到 Dify 请求
 *
 * @param file       文件
 * @param resourceId 资源ID
 * @param keyType    密钥类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSyncDifyReq {
    private MultipartFile file;
    private String resourceId;
    private String keyType;
}
