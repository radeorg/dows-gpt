package org.dows.gpt.service;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dows.gpt.entity.GptFileEntity;
import org.dows.gpt.entity.GptLockEntity;
import org.dows.gpt.entity.GptTokenEntity;
import org.dows.gpt.mapper.GptLockMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @ClassName AsyncGptRecorder
 * @Description TODO
 * @Author jack.china.ye
 * @Date 2025/8/11 23:53
 */
@Slf4j
@Service
public class AsyncGptRecorder {

    @Resource
    private GptFileService gptFileService;

    @Resource
    private GptTokenService gptTokenService;

    @Resource
    private GptLockMapper gptLockMapper; // 使用 mapper 的原子方法


    /**
     * 异步记录：插入 GptFile、GptToken，并原子更新 GptLock（usedTokens 与 locked）。
     * 该方法独立事务，确保即使主线程返回，写库也会尝试提交。
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAndMaybeLock(String appId,
                                   Long operatorId,
                                   String jdName,
                                   String fileName,
                                   Long orgRootId,
                                   Long orgTreeId,
                                   long inputToken,
                                   long outputToken) {
        try {
            Date now = new Date();
            long delta = inputToken + outputToken;

            // 1) 插入 GptFile（如果前端可能只传 jdName 或 fileName，这里按传啥插啥）
            GptFileEntity fileEntity = GptFileEntity.builder()
                    .countFileId(IdUtil.getSnowflakeNextId())
                    .appId(appId)
                    .operatorId(operatorId)
                    .jdName(jdName)
                    .fileName(fileName)
                    .orgRootId(orgRootId)
                    .orgTreeId(orgTreeId)
                    .deleted(0)
                    .ts(now)
                    .ut(now)
                    .build();
            gptFileService.save(fileEntity);

            // 2) 插入 GptToken（关联上面生成的 countFileId）
            GptTokenEntity tokenEntity = GptTokenEntity.builder()
                    .countTokenId(IdUtil.getSnowflakeNextId())
                    .countFileId(fileEntity.getCountFileId())
                    .inputToken(inputToken)
                    .outputToken(outputToken)
                    .appId(appId)
                    .operatorId(operatorId)
                    .deleted(0)
                    .ts(now)
                    .ut(now)
                    .build();
            gptTokenService.save(tokenEntity);

            // 3) 原子更新 GptLock（如果存在）
            Integer lock = gptLockMapper.selectByAppId(appId);
            if (lock != null) {
                int rows = gptLockMapper.atomicAddUsedTokens(appId);
                if (rows > 0) {
                    log.info("appId={} usedTokens += {}，原子更新成功。", appId, delta);
                } else {
                    // 一般不会发生，除非 WHERE 未命中。记录日志便于排查。
                    log.warn("appId={} 原子更新 failed (rows=0). lock exists? {}, delta={}", appId, lock != null, delta);
                }
            } else {
                // 没有 lock 配置：按需处理 -> 这里选择记录日志（如果需自动创建 lock 请在此加入插入逻辑）
                log.debug("appId={} 无 gpt_lock 配置，跳过 lock 更新", appId);
            }

        } catch (Exception e) {
            // 异步中遇到异常只记录日志（避免抛给调用线程）
            log.error("异步记录 GptFile/GptToken/GptLock 出错", e);
            // 根据你业务需要也可以把失败信息写入一个失败表，用于重试或告警
        }
    }

    Integer getLocked(String appId){
        return gptLockMapper.selectByAppId(appId);
    }
}
