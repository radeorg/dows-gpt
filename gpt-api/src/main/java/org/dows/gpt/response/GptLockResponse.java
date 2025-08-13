package org.dows.gpt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName GptLockResponse
 * @Description TODO
 * @Author jack.china.ye
 * @Date 2025/8/13 22:26
 */

@Data
public class GptLockResponse {

    @Schema(description = "已使用 token 数")
    private Long usedTokens;

    @Schema(description = "token 数")
    private Long tokenSize;

    @Schema(description = "开始时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "是否锁定")
    private Integer locked;

    @Schema(description = "更新时间")
    private Date ut;

}
