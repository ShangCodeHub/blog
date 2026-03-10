package com.shang.dify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 红头文件解析响应
 *
 * @param researchField      研究领域
 * @param researchDirection  研究方向
 * @param researchTopic     研究课题
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedHeaderFileParseResp {
    private String researchField;
    private String researchDirection;
    private String researchTopic;
}
