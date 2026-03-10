package com.shang.dify.controller;

import com.shang.dify.common.controller.BaseController;
import com.shang.dify.entity.DifyApiKey;
import com.shang.dify.mapper.DifyApiKeyMapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dify API 密钥管理控制器
 *
 * @author shihang.shang
 * @className DifyApiKeyController
 * @date 2025-01-28 12:30
 */
@Slf4j
@RestController
@RequestMapping("/api/dify/keys")
@RequiredArgsConstructor
@Api(tags = "Dify API 密钥管理")
public class DifyApiKeyController extends BaseController<DifyApiKeyMapper, DifyApiKey> {

}