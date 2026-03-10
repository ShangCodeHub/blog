package com.shang.dto.feedback;

import com.shang.entity.SysFeedback;
import lombok.Data;

/**
 * @author: quequnlong
 * @date: 2025/1/12
 * @description:
 */
@Data
public class SysFeedbackQueryDto extends SysFeedback {

    private String source;
}
