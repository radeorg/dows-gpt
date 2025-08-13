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
@Schema(name = "GPT Token表")
@Table(value = "gpt_token")
public class GptTokenEntity extends BaseEntity<GptTokenEntity> {

    @Schema(description = "统计tokenID")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Column(value = "count_token_id")
    private Long countTokenId;

    @Schema(description = "统计文件ID")
    @Column(value = "count_file_id")
    private Long countFileId;

    @Schema(description = "输入token")
    @Column(value = "input_token")
    private Long inputToken;

    @Schema(description = "输出token")
    @Column(value = "output_token")
    private Long outputToken;

    @Schema(description = "应用ID")
    @Column(value = "app_id", tenantId = true)
    private String appId;

    @Schema(description = "操作者ID")
    @Column(value = "operator_id")
    private Long operatorId;

    @Schema(description = "逻辑删除 0未删除 1删除")
    @Column(value = "deleted", isLogicDelete = true)
    private Integer deleted;

    @Schema(description = "操作时间")
    @Column(value = "ts")
    private Date ts;

    @Schema(description = "更新时间")
    @Column(value = "ut")
    private Date ut;
}


