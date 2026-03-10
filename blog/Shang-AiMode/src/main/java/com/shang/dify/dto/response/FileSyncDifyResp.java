package com.shang.dify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件同步到 Dify 响应
 *
 * @param difyFileId String Dify 文件ID
 * @author JiaWen.Wu
 * @className FileSyncDifyResp
 * @date 2025-01-26 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSyncDifyResp {
    private String difyFileId;
}
