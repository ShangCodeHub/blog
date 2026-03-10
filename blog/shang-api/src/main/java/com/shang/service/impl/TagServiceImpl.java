package com.shang.service.impl;

import com.shang.service.TagService;
import com.shang.vo.tag.TagListVo;
import com.shang.mapper.SysTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final SysTagMapper sysTagMapper;

    @Override
    public List<TagListVo> getTagsApi() {
        return sysTagMapper.getTagsApi();
    }
}
