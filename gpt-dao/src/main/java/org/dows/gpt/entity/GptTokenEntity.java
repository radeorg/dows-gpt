package org.dows.gpt.entity;

import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.dows.rade.crud.AutoFillDataListener;
import org.dows.rade.crud.BaseEntity;

import java.util.Date;

/**
 * token(GptToken)实体类
 *
 * @author lait.zhang@gmail.com
 * @since 2025-08-10 17:39:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "账号标识表")
@Table(value = "gpt_token", onUpdate = AutoFillDataListener.class, onInsert = AutoFillDataListener.class)

public class GptTokenEntity extends BaseEntity<GptTokenEntity> {
    /**
     * 统计tokenID
     */
    private Long countTokenId;
    /**
     * 统计文件ID
     */
    private Long countFileId;
    /**
     * 输入token
     */
    private Long inputToken;
    /**
     * 输出token
     */
    private Long outputToken;
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

