package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 字符串数组输入值
 *
 * @param values List<String> 字符串列表
 * @author JiaWen.Wu
 * @className DifyStringArrayInput
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyStringArrayInput implements DifyInputValue {
    private List<String> values;
}
