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
 * 文件(GptFile)实体类
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:39:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "账号标识表")
@Table(value = "gpt_file", onUpdate = AutoFillDataListener.class, onInsert = AutoFillDataListener.class)

public class GptFileEntity extends BaseEntity<GptFileEntity> {
    /**
     * 统计文件ID
     */
    @Schema(description = "统计文件ID")
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long countFileId;
    /**
     * 组织根ID
     */
    private Long orgRootId;
    /**
     * 组织树ID
     */
    private Long orgTreeId;

    /**
     * JD名
     */
    private String jdName;
    /**
     * 简历文件名
     */
    private String fileName;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 操作者ID
     */
    private Long operatorId;
    /**
     * 逻辑删除 0未删除 1删除
     */
    private Integer deleted;
    /**
     * 操作时间
     */
    private Date ts;
    /**
     * 更新时间
     */
    private Date ut;


}

