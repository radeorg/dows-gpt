package org.dows.gpt.service.impl;

import org.dows.gpt.entity.GptFileEntity;
import org.dows.gpt.mapper.GptFileMapper;
import org.dows.gpt.service.GptFileService;
import org.dows.rade.crud.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文件(GptFile)表服务实现类
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:39:03
 */
@Service("gptFileService")
public class GptFileServiceImpl extends BaseServiceImpl<GptFileMapper, GptFileEntity> implements GptFileService {

}
