package com.shang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shang.vo.comment.CommentListVo;
import com.shang.entity.SysComment;

public interface CommentService {

    /**
     * 获取评论列表
     * @param articleId
     * @return
     */

    IPage<CommentListVo> getComments(Integer articleId,String sortType);

    /**
     * 新增评论
     * @param sysComment
     * @return
     */
    Boolean add(SysComment sysComment);
}
