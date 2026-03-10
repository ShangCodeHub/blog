package com.shang.dify.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建 Dify Chatbot 应用请求
 *
 * @author
 * @since 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Dify Chatbot 应用创建请求")
public class DifyChatbotAppRequest extends BaseDifyRequest {

    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称", example = "test")
    private String name;

    /**
     * 应用模式
     */
    @ApiModelProperty(value = "应用模式", example = "chat")
    private String mode;

    /**
     * 应用描述
     */
    @ApiModelProperty(value = "应用描述", example = "testte")
    private String description;

    /**
     * 图标内容
     */
    @ApiModelProperty(value = "应用图标", example = "🤖")
    private String icon;

    /**
     * 图标背景色
     */
    @ApiModelProperty(value = "图标背景颜色", example = "#FFEAD5")
    private String icon_background;

    /**
     * 图标类型
     */
    @ApiModelProperty(value = "图标类型", example = "emoji")
    private String icon_type;
}

