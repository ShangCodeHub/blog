package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Dify 文件上传响应 DTO
 *
 * @author shihang.shang
 * @className DifyFileUploadResponse
 * @date 2025-01-28 12:00
 */
@Data
@ApiModel(description = "Dify 文件上传响应结果")
public class DifyFileUploadResponse {

    @ApiModelProperty(value = "文件ID")
    private String id;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long size;

    @ApiModelProperty(value = "文件扩展名")
    private String extension;

    @ApiModelProperty(value = "MIME类型")
    private String mimeType;

    @ApiModelProperty(value = "创建者ID")
    private String createdBy;

    @ApiModelProperty(value = "创建时间")
    private Long createdAt;
}
