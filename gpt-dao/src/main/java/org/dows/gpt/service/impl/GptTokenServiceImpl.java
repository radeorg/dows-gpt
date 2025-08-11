package org.dows.gpt.service.impl;

import org.dows.gpt.entity.GptTokenEntity;
import org.dows.gpt.mapper.GptTokenMapper;
import org.dows.gpt.service.GptTokenService;
import org.dows.rade.crud.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * token(GptToken)表服务实现类
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:39:04
 */
@Service("gptTokenService")
public class GptTokenServiceImpl  extends BaseServiceImpl<GptTokenMapper, GptTokenEntity> implements GptTokenService {

}
