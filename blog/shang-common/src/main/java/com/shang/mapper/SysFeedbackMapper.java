package com.shang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shang.dto.feedback.SysFeedbackQueryDto;
import com.shang.entity.SysFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shang.vo.feedback.SysFeedbackVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 反馈表 Mapper接口
 */
@Mapper
public interface SysFeedbackMapper extends BaseMapper<SysFeedback> {
    /**
     * 分页查询
     * @param page
     * @param sysFeedback
     * @return
     */
    IPage<SysFeedbackVo> page(@Param("page") Page<Object> page, @Param("sysFeedback") SysFeedbackQueryDto sysFeedback);
}
