package org.dows.gpt.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.dows.rade.crud.AutoFillDataListener;
import org.dows.rade.crud.BaseEntity;

import java.util.Date;

/**
 * 租户锁(TenantLock)实体类
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:51:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "锁表")
@Table(value = "gpt_lock", onUpdate = AutoFillDataListener.class, onInsert = AutoFillDataListener.class)
public class GptLockEntity extends BaseEntity<GptLockEntity> {
    /**
     * 租户锁Id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long gptLockId;
    /**
     * token数
     */
    private Long tokenSize;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 是否锁定
     */
    private Integer locked;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 时间戳
     */
    private Date ts;
    /**
     * 更新时间
     */
    private Date ut;
    /**
     * 操作者ID
     */
    private Long operatorId;

}

