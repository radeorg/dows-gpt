package org.dows.gpt.service.impl;

import org.dows.gpt.entity.GptLockEntity;
import org.dows.gpt.mapper.GptLockMapper;
import org.dows.gpt.service.GptLockService;
import org.dows.rade.crud.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 租户锁(TenantLock)表服务实现类
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:51:19
 */
@Service("gptLockService")
public class GptLockServiceImpl extends BaseServiceImpl<GptLockMapper, GptLockEntity> implements GptLockService {
}
