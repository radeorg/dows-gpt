package org.dows.gpt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName GptFileReponse
 * @Description TODO
 * @Author jack.china.ye
 * @Date 2025/8/13 23:41
 */

@Data
public class GptFileReponse {

    @Schema(description = "统计文件ID")
    private Long countFileId;

    @Schema(description = "组织根ID")
    private Long orgRootId;

    @Schema(description = "组织树ID")
    private Long orgTreeId;

    @Schema(description = "岗位名称")
    private String jdName;

    @Schema(description = "简历文件名")
    private String fileName;

    @Schema(description = "1:jd;2:简历,3:匹配")
    private Long gptType;

    @Schema(description = "输入token")
    private Long inputToken;

    @Schema(description = "输出token")
    private Long outputToken;

    @Schema(description = "应用ID")
    private String appId;

}
