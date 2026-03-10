package com.shang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shang.entity.SysWebConfig;
import com.shang.mapper.SysWebConfigMapper;
import com.shang.service.SysWebConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysWebConfigServiceImpl extends ServiceImpl<SysWebConfigMapper, SysWebConfig> implements SysWebConfigService {
}
