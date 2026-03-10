package com.shang.controller.comment;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shang.service.CommentService;
import com.shang.vo.comment.CommentListVo;
import com.shang.common.Result;
import com.shang.entity.SysComment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "门户-评论管理")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/comment/list")
    @ApiOperation(value = "获取文章评论列表")
    public Result<IPage<CommentListVo>> getComments(Integer articleId,String sortType) {
        return Result.success(commentService.getComments(articleId,sortType));
    }

    @PostMapping("/addComment")
    @ApiOperation(value = "获取文章评论列表")
    public Result<Boolean> add(@RequestBody SysComment sysComment) {
        return Result.success(commentService.add(sysComment));
    }
}
