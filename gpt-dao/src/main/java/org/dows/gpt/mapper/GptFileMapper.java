package org.dows.gpt.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dows.gpt.entity.GptFileEntity;

/**
 * 租户锁(TenantLock)表数据库访问层
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:51:18
 */
@Mapper
public interface GptFileMapper extends BaseMapper<GptFileEntity> {

}

