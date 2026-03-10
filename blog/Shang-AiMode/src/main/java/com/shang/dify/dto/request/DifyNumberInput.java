package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数字输入值
 *
 * @param value Number 数字值
 * @author JiaWen.Wu
 * @className DifyNumberInput
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyNumberInput implements DifyInputValue {
    private Number value;
}
