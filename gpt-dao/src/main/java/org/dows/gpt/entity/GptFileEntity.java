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
@Schema(name = "GPT文件表")
@Table(value = "gpt_file")
public class GptFileEntity extends BaseEntity<GptFileEntity> {

    @Schema(description = "统计文件ID")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    @Column(value = "count_file_id")
    private Long countFileId;

    @Schema(description = "组织根ID")
    @Column(value = "org_root_id")
    private Long orgRootId;

    @Schema(description = "组织树ID")
    @Column(value = "org_tree_id")
    private Long orgTreeId;

    @Schema(description = "岗位名称")
    @Column(value = "jd_name")
    private String jdName;

    @Schema(description = "简历文件名")
    @Column(value = "file_name")
    private String fileName;

    @Schema(description = "1:jd;2:简历,3:匹配")
    @Column(value = "gpt_type")
    private Long gptType;

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




