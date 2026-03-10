package com.shang.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shang.common.Constants;
import com.shang.common.Result;
import com.shang.entity.FileDetail;
import com.shang.entity.SysFileOss;
import com.shang.exception.ServiceException;
import com.shang.service.AISendMessageService;
import com.shang.service.FileDetailService;
import com.shang.utils.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/file")
@Api(tags = "文件管理")
@RequiredArgsConstructor
public class FileController {

    private final FileDetailService fileDetailService;
    private final FileStorageService fileStorageService;
    @Resource
    AISendMessageService aiSendMessageService;
    @Value("${UploadPath}")
    String  localFile  ;

    @SaCheckLogin
    @GetMapping("/list")
    @ApiOperation(value = "获取文件记录表列表")
    public Result<IPage<FileDetail>> list(FileDetail fileDetail) {
        return Result.success(fileDetailService.selectPage(fileDetail));
    }

    @SaCheckLogin
    @GetMapping("/getOssConfig")
    @ApiOperation(value = "获取存储平台配置")
    public Result<List<SysFileOss>> getOssConfig() {
        return Result.success(fileDetailService.getOssConfig());
    }

    @SaCheckLogin
    @PostMapping("/addOss")
    @SaCheckPermission("sys:oss:submit")
    @ApiOperation(value = "添加存储平台配置")
    public Result<Void> addOss(@RequestBody SysFileOss sysFileOss) {
        fileDetailService.addOss(sysFileOss);
        if (sysFileOss.getIsEnable() == Constants.YES) {
            fileStorageService.getProperties().setDefaultPlatform(sysFileOss.getPlatform());
        }
        return Result.success();
    }

    @SaCheckLogin
    @PutMapping("/updateOss")
    @SaCheckPermission("sys:oss:submit")
    @ApiOperation(value = "修改存储平台配置")
    public Result<Void> updateOss(@RequestBody SysFileOss sysFileOss) {
        fileDetailService.updateOss(sysFileOss);
        if (sysFileOss.getIsEnable() == Constants.YES) {
            fileStorageService.getProperties().setDefaultPlatform(sysFileOss.getPlatform());
        }
        return Result.success();
    }

    @SaCheckLogin
    @PostMapping("/upload")
    @ApiOperation(value = "上传文件")
    public Result<String> upload(MultipartFile file, String source) {
        String path = DateUtil.parseDateToStr(DateUtil.YYYYMMDD, DateUtil.getNowDate()) + "/";
        //这个source可在前端上传文件时提供，可用来区分是头像还是文章图片等
        if (StringUtils.isNotBlank(source)) {
            path = path + source + "/";
        }
        // 获取原始文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            int dotIndex = originalFilename.lastIndexOf(".");
            fileExtension = originalFilename.substring(dotIndex); // 包含点号
            originalFilename = originalFilename.substring(0, dotIndex);
        }
        // 生成唯一文件名：原文件名 + UUID + 时间戳 + 后缀
        String uniqueFilename = originalFilename + "_" + System.currentTimeMillis() + fileExtension;
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(path)
                .setPlatform("local-plus-1")
                .setSaveFilename(uniqueFilename) // 使用唯一文件名
                .putAttr("source", source)
                .upload();

        if (fileInfo == null) {
            throw new ServiceException("上传文件失败");
        }
        return Result.success(fileInfo.getUrl());
    }

//    @SaCheckLogin
    @PostMapping("/generatedimg")
    @ApiOperation(value = "生成图片")
        public Result<String> generatedimg(String articleTitle, String source) {
        String url = aiSendMessageService.GetGenerateImageAdress(articleTitle, source);
        String path = DateUtil.parseDateToStr(DateUtil.YYYYMMDD, DateUtil.getNowDate()) + "/";
        if (StringUtils.isNotBlank(source)) {
            path = path + source + "/";
        }
//        String dirPath = localFile + path;
//        File dir = new File(dirPath);
//        if (!dir.exists()) {
//            dir.mkdirs(); // 递归创建多级目录
//        }
        String uniqueFilename =System.currentTimeMillis()+".png";
        FileInfo fileInfo = fileStorageService.of(url)
                .setPath(path+"AiGeneratedImg/")
                .setPlatform("local-plus-1")
                .setSaveFilename(uniqueFilename) // 使用唯一文件名
                .putAttr("source", source)
                .upload();
        return Result.success(fileInfo.getUrl());
    }
    @GetMapping("/delete")
    @ApiOperation(value = "删除文件")
    @SaCheckPermission("sys:file:delete")
    public Result<Boolean> delete(String url) {
        boolean flag = fileStorageService.delete(url);
        if (flag) {
            fileDetailService.delete(url);
        }
        return Result.success();
    }
}