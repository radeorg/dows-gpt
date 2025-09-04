package org.dows.gpt.internlm.response;

import lombok.Data;

/**
 * 使用量
 *
 */
@Data
public class Usage {

    /**
     * 提示词token
     */
    private int promptTokens;

    /**
     * 输出token
     */
    private int completionTokens;

    /**
     * 总token
     */
    private int totalTokens;

}
