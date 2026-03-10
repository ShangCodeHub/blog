package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字符串输入值
 *
 * @param value String 字符串值
 * @author JiaWen.Wu
 * @className DifyStringInput
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyStringInput implements DifyInputValue {
    private String value;
}
