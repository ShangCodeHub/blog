package com.shang.dify.controller;
import com.shang.dify.dto.*;
import com.shang.dify.service.DifyApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Dify API 简化控制器
 * 使用 DifyApiClient 工具类
 * 
 * @author shihang.shang
 * @since 2024-10-22
 */
@Slf4j
@RestController
@RequestMapping("/api/dify")
@RequiredArgsConstructor
@Api(tags = "Dify API")
public class DifyApiController {
    private final DifyApiService difyApiService;
    /**
     * 获取数据集列表
     */
    @PostMapping("/datasets/list")
    @ApiOperation(value = "获取数据集列表")
    public ResponseEntity<String> getDatasets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @Valid @RequestBody BaseDifyRequest request) {
        return difyApiService.getDatasets(page, limit, request.getUserId(), request.getResourceId(), request.getKeyType());
    }
    /**
     * 创建数据集
     */
    @PostMapping("/datasets")
    @ApiOperation(value = "创建数据集")
    public ResponseEntity<String> createDataset(@Valid @RequestBody DifyDatasetRequest request) {
        return difyApiService.createDataset(request, request.getUserId(), request.getResourceId(), request.getKeyType());
    }
    
    /**
     * 获取数据集详情
     */
    @PostMapping("/datasets/{datasetId}")
    @ApiOperation(value = "获取数据集详情")
    public ResponseEntity<String> getDataset(
            @PathVariable String datasetId,
            @Valid @RequestBody BaseDifyRequest request) {
        return difyApiService.getDataset(datasetId, request.getUserId(), request.getResourceId(), request.getKeyType());
    }
    
    /**
     * 更新数据集
     */
    @PutMapping("/datasets/{datasetId}")
    @ApiOperation(value = "更新数据集")
    public ResponseEntity<String> updateDataset(
            @PathVariable String datasetId,
            @Valid @RequestBody DifyDatasetRequest request) {
        return difyApiService.updateDataset(datasetId, request, request.getUserId(), request.getResourceId(), request.getKeyType());
    }

    /**
     * 删除数据集
     */
    @PostMapping("/datasets/{datasetId}/delete")
    @ApiOperation(value = "删除数据集")
    public ResponseEntity<String> deleteDataset(
            @PathVariable String datasetId,
            @Valid @RequestBody BaseDifyRequest request) {
        return difyApiService.deleteDataset(datasetId, request.getUserId(), request.getResourceId(), request.getKeyType());
    }
    /**
     * 检索知识库
     */
    @PostMapping("/datasets/{datasetId}/retrieve")
    @ApiOperation(value = "检索知识库")
    public ResponseEntity<String> retrieveDataset(
            @PathVariable String datasetId,
            @Valid @RequestBody DifyRetrieveRequest request) {
        return difyApiService.retrieveDataset(datasetId, request, request.getUserId(), request.getResourceId(), request.getKeyType());
    }
//    /**
//     * 上传文档到数据集 - 先存储文件，再调用Dify API
//     */
//    @PostMapping("/datasets/{datasetId}/document/upload")
//    @Operation(summary = "上传文档到数据集（先存储文件，再调用Dify API）")
//    public CompletableFuture<List<ResponseEntity<String>>> uploadDocument(
//            @PathVariable String datasetId,
//            @RequestParam("file") List<MultipartFile> file,
//            @RequestParam("userId") Long userId,
//            @RequestParam("resourceId") String resourceId,
//            @RequestParam("keyType") String keyType) {
//        return difyApiService.uploadDocumentsAsync(datasetId, file, userId, resourceId, keyType);
//    }
    /**
     * 上传文件
     */
    @PostMapping("/files/upload")
    @ApiOperation(value = "上传文件到 Dify")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("resourceId") String resourceId,
            @RequestParam("keyType") String keyType) {
        return difyApiService.uploadFileWithDynamicKey(userId, file, userId, resourceId);
    }
}