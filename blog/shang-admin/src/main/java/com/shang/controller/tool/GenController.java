package com.shang.controller.tool;

import com.shang.common.Result;
import com.shang.entity.GenTable;
import com.shang.service.GenTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/tool/gen")
@RequiredArgsConstructor
@Api(tags = "代码生成业务")
public class GenController {

    private final GenTableService genTableService;

    @GetMapping("/list")
    @ApiOperation(value = "获取代码生成列表")
    public Result<Map<String, Object>> list(GenTable genTable) {
        return Result.success(genTableService.selectGenTableList(genTable));
    }

    @GetMapping("/preview/{tableId}")
    @ApiOperation(value = "预览代码")
    public Result<Map<String, String>> preview(@PathVariable Long tableId) {
        return Result.success(genTableService.previewCode(tableId));
    }

    @DeleteMapping("/{tableIds}")
    @ApiOperation(value = "删除代码生成")
    public Result<Integer> remove(@PathVariable Long[] tableIds) {
        return Result.success(genTableService.deleteGenTableByIds(tableIds));
    }

    @GetMapping("/code/{tables}")
    @ApiOperation(value = "下载代码")
    public Result<Void> genCode(@PathVariable String tables) {
        genTableService.generatorCode(tables);
        return Result.success();
    }

    @GetMapping("/sync/{tableName}")
    @ApiOperation(value = "同步数据库")
    public Result<String> synchDb(@PathVariable String tableName) {
        return Result.success(genTableService.synchDb(tableName));
    }

    @GetMapping("/db/list")
    @ApiOperation(value = "查询数据库列表")
    public Result<Map<String, Object>> dbList(GenTable genTable) {
        Map<String, Object> list = genTableService.selectDbTableList(genTable);
        return Result.success(list);
    }

    @PostMapping("/importTable")
    @ApiOperation(value = "导入表结构")
    public Result<Void> importTable(@RequestBody String[] tables) {
        genTableService.importGenTable(tables);
        return Result.success();
    }

    @GetMapping("/download/{tables}")
    @ApiOperation(value = "下载代码")
    public void download(@PathVariable("tables") String tables, HttpServletResponse response) {
        try {
            byte[] data = genTableService.downloadCode(tables.split(","));

            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");

            IOUtils.write(data, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("下载代码失败", e);
        }
    }
}
