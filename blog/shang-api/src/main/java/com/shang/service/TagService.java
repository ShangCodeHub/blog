package com.shang.service;

import com.shang.vo.tag.TagListVo;

import java.util.List;

public interface TagService {

    /**
     * 获取标签列表
     * @return
     */
    List<TagListVo> getTagsApi();

}
