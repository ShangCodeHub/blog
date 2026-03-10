package com.shang.dify.mapper;

import com.shang.dify.common.mapper.MyBaseMapper;
import com.shang.dify.entity.DifyApiKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * Dify API 密钥 Mapper 接口
 *
 * @author shihang.shang
 * @className DifyApiKeyMapper
 * @date 2025-01-28 12:30
 */
@Mapper
public interface DifyApiKeyMapper extends MyBaseMapper<DifyApiKey> {

}
