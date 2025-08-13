package org.dows.gpt.entity;

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.dows.rade.crud.BaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GPT锁表")
@Table(value = "gpt_lock")
public class GptLockEntity extends BaseEntity<GptLockEntity> {

    @Schema(description = "锁ID")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Column(value = "gpt_lock_id")
    private Long gptLockId;

    @Schema(description = "已使用 token 数")
    @Column(value = "used_tokens")
    private Long usedTokens;

    @Schema(description = "token 数")
    @Column(value = "token_size")
    private Long tokenSize;

    @Schema(description = "开始时间")
    @Column(value = "start_time")
    private Date startTime;

    @Schema(description = "结束时间")
    @Column(value = "end_time")
    private Date endTime;

    @Schema(description = "是否锁定")
    @Column(value = "locked")
    private Integer locked;

    @Schema(description = "应用ID")
    @Column(value = "app_id", tenantId = true)
    private String appId;

    @Schema(description = "时间戳")
    @Column(value = "ts")
    private Date ts;

    @Schema(description = "更新时间")
    @Column(value = "ut")
    private Date ut;

    @Schema(description = "操作者ID")
    @Column(value = "operator_id")
    private Long operatorId;
}


