package com.shang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shang.vo.moment.MomentPageVo;

/**
 * @author: quequnlong
 * @date: 2025/2/5
 * @description:
 */
public interface MomentService {
    IPage<MomentPageVo> getMomentList();

}
