package com.shang.dify.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件数组输入值
 *
 * @param files List<DifyFileInput> 文件列表
 * @author JiaWen.Wu
 * @className DifyFileArrayInput
 * @date 2025-01-26 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyFileArrayInput implements DifyInputValue {
    private List<DifyFileInput> files;
}
