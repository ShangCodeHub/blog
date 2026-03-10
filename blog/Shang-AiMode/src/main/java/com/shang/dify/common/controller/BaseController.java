package com.shang.dify.common.controller;

import com.shang.dify.common.entity.BaseEntity;
import com.shang.dify.common.mapper.MyBaseMapper;
import com.shang.dify.common.service.BaseServiceImpl;
import com.shang.dify.common.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@SuppressWarnings("all")
@Api(tags = "base")
public abstract class BaseController<M extends MyBaseMapper<T>,T extends BaseEntity> {

    @Autowired
    BaseServiceImpl<M,T> service;

    @PostMapping("/list")
    @ApiOperation(value = "获取列表", notes = "获取列表")
    public ResultVO list(@RequestBody T entity) {
        if (entity == null) {
            //  throw new HttpMessageNotReadableException("请求体不能为空", null);
        }
        return service.getWhereList(entity);
    }

//    @Autowired
//    public BaseController(@Qualifier("UserServiceImpl") S service) {
//        this.service = service;
//    }
    @PostMapping("/save")
    @ApiOperation(value = "新增数据", notes = "新增数据")
    public ResultVO save(@RequestBody T entity) {
        if (entity == null) {
            //  throw new HttpMessageNotReadableException("请求体不能为空", null);
        }
        return ResultVO.ok( service.save(entity)) ;
    }

    @PutMapping("/update")
    @ApiOperation(value = "根据id修改数据", notes = "根据id修改数据")
    public ResultVO update(@RequestBody T entity) {
        return ResultVO.ok( service.updateById(entity))  ;
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "根据id删除数据", notes = "根据id删除数据")
    public ResultVO delete(@PathVariable Long id) {
        return  ResultVO.ok( service.removeById(id)) ;
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "根据id获取数据", notes = "根据id获取数据")
    public ResultVO getById(@PathVariable Long id) {
        return  ResultVO.ok( service.getById(id)) ;
    }


}
