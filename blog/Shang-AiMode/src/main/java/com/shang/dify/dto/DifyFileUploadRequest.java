package com.shang.dify.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dify 文件上传请求 DTO
 *
 * @author shihang.shang
 * @className DifyFileUploadRequest
 * @date 2025-01-28 12:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Dify 文件上传请求参数")
public class DifyFileUploadRequest extends BaseDifyRequest {

    /**
     * 用户标识
     */
    @ApiModelProperty(value = "用户标识", required = true, example = "workflow_user_001")
    private String user;

    /**
     * 上传的文件
     */
    @ApiModelProperty(value = "上传的文件", required = true)
    private MultipartFile file;
}