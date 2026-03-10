package com.shang.dify.controller;

import com.shang.dify.dto.DifyWorkflowLogsRequest;
import com.shang.dify.dto.DifyWorkflowRequest;
import com.shang.dify.dto.DifyWorkflowStatusRequest;
import com.shang.dify.dto.request.DifyWorkflowDraftGetReq;
import com.shang.dify.dto.request.DifyWorkflowDraftUpdateReqParam;
import com.shang.dify.dto.request.DifyWorkflowUpdateAndPublishReq;
import com.shang.dify.dto.response.DifyWorkflowDraftResp;
import com.shang.dify.service.DifyApiService;
import com.shang.dify.service.DifyWorkflowService;
import com.shang.dify.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Dify Workflow 工作流控制器
 *
 * @author shihangshang
 * @className DifyWorkflowController
 * @date 2025-11-18 11:00
 */
@Slf4j
@RestController
@RequestMapping("/api/dify/workflows")
@RequiredArgsConstructor
@Api(tags = "Dify Workflow API")
public class DifyWorkflowController {

    private final DifyApiService difyApiService;
    private final DifyWorkflowService difyWorkflowService;

    /**
     * 执行 Dify 工作流
     * 
     * 请求示例：
     * {
     *   "inputs": {
     *     "file": [{
     *       "type": "document",
     *       "transfer_method": "local_file",
     *       "upload_file_id": "0c3b29df-def6-4850-aa68-693fc55e5413"
     *     }]
     *   },
     *   "response_mode": "blocking",
     *   "user": "abc-123",
     *   "userId": 1,
     *   "resourceId": "workflow-id",
     *   "keyType": "workflow"
     * }
     *
     * @param request 工作流请求
     * @return 工作流执行结果
     */
    @PostMapping("/run")
    @ApiOperation(value = "执行 Dify 工作流", notes = "执行 Dify 工作流，支持阻塞和流式两种响应模式")
    public ResponseEntity<String> runWorkflow(@Valid @RequestBody DifyWorkflowRequest request) {
        log.info("执行 Dify 工作流，用户: {}, 工作流ID: {}", request.getUserId(), request.getResourceId());
        return difyApiService.runWorkflowWithDynamicKey(request, request.getUserId(), request.getResourceId());
    }

    /**
     * 获取工作流运行状态
     *
     * @param request 工作流状态请求
     * @return 工作流运行状态
     */
    @PostMapping("/run/status")
    @ApiOperation(value = "获取工作流运行状态", notes = "根据工作流运行ID获取执行状态")
    public ResponseEntity<String> getWorkflowRunStatus(@Valid @RequestBody DifyWorkflowStatusRequest request) {
        log.info("获取工作流运行状态，运行ID: {}, 用户: {}", request.getWorkflowRunId(), request.getUserId());
        return difyApiService.getWorkflowRunStatusWithDynamicKey(
                request.getWorkflowRunId(), 
                request.getUserId(), 
                request.getResourceId());
    }

    /**
     * 获取工作流日志
     *
     * @param request 工作流日志请求
     * @return 工作流日志列表
     */
    @PostMapping("/logs")
    @ApiOperation(value = "获取工作流日志", notes = "分页获取工作流执行日志")
    public ResponseEntity<String> getWorkflowLogs(@Valid @RequestBody DifyWorkflowLogsRequest request) {
        log.info("获取工作流日志，页码: {}, 每页数量: {}, 用户: {}", 
                request.getPage(), request.getLimit(), request.getUserId());
        return difyApiService.getWorkflowLogsWithDynamicKey(
                request.getPage(), 
                request.getLimit(), 
                request.getUserId(), 
                request.getResourceId());
    }

    /**
     * 获取工作流 Draft 配置
     * GET /api/dify/workflows/{appId}/draft
     *
     * @param request 获取请求参数
     * @return 工作流 Draft 配置响应
     */
    @PostMapping("/draft/get")
    @ApiOperation(value = "获取工作流 Draft 配置", notes = "获取工作流的 draft 版本配置，包含 hash 值")
    public Result<DifyWorkflowDraftResp> getWorkflowDraft(@Valid @RequestBody DifyWorkflowDraftGetReq request) {
        log.info(String.format("获取工作流 Draft 配置: appId=%s", request.getAppId()));
        DifyWorkflowDraftResp draftResp = difyWorkflowService.getWorkflowDraft(request.getAppId());
        return Result.success(draftResp);
    }

    /**
     * 更新工作流 Draft 配置
     * POST /api/dify/workflows/{appId}/draft
     * 主要修改 knowledge-retrieval 类型节点的 dataset_ids
     *
     * @param request 更新请求参数
     * @return 更新结果响应
     */
    @PostMapping("/draft/update")
    @ApiOperation(value = "更新工作流 Draft 配置", notes = "更新工作流的 draft 版本配置，主要修改 knowledge-retrieval 节点的 dataset_ids，hash 值必须与 GET 返回的一致")
    public Result<String> updateWorkflowDraft(@Valid @RequestBody DifyWorkflowDraftUpdateReqParam request) {
        log.info(String.format("更新工作流 Draft 配置: appId=%s, datasetIds=%s",
                request.getAppId(), request.getDatasetIds()));
        String responseBody = difyWorkflowService.updateWorkflowDraft(
                request.getAppId(),
                request.getDatasetIds());
        return Result.success(responseBody);
    }

    /**
     * 更新并发布工作流（综合接口）
     * 流程：1. GET 获取工作流详情（包含 hash）
     *       2. 使用 GET 返回的 hash 更新工作流
     *       3. 更新成功后，再次 GET 获取更新后的 hash
     *       4. 使用更新后的 hash 调用 publish 接口发布工作流
     *
     * @param request 更新并发布请求参数
     * @return 发布结果响应
     */
    @PostMapping("/draft/update-and-publish")
    @ApiOperation(value = "更新并发布工作流", notes = "综合接口：先获取工作流详情，然后更新 knowledge-retrieval 节点的 dataset_ids，最后发布工作流")
    public Result<String> updateAndPublishWorkflow(@Valid @RequestBody DifyWorkflowUpdateAndPublishReq request) {
        log.info(String.format("更新并发布工作流: appId=%s, datasetIds=%s, markedName=%s, markedComment=%s",
                request.getAppId(), request.getDatasetIds(), request.getMarkedName(), request.getMarkedComment()));
        String responseBody = difyWorkflowService.updateAndPublishWorkflow(
                request.getAppId(),
                request.getDatasetIds(),
                request.getMarkedName(),
                request.getMarkedComment());
        return Result.success(responseBody);
    }
}