package org.dows.gpt.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.dows.gpt.entity.GptLockEntity;

/**
 * 租户锁(TenantLock)表数据库访问层
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:51:18
 */
@Mapper
public interface GptLockMapper extends BaseMapper<GptLockEntity> {
    @Update("""
        UPDATE gpt_lock
        SET used_tokens = COALESCE(used_tokens, 0) + #{delta},
            locked = CASE WHEN COALESCE(used_tokens, 0) + #{delta} >= token_size THEN 1 ELSE locked END,
            ut = NOW(),
            operator_id = #{operatorId}
        WHERE app_id = #{appId}
    """)
    int atomicAddUsedTokens(@Param("appId") String appId,
                            @Param("delta") long delta,
                            @Param("operatorId") Long operatorId);

    @Select("SELECT * FROM gpt_lock WHERE app_id = #{appId} LIMIT 1")
    GptLockEntity selectByAppId(@Param("appId") String appId);

}

