package org.dows.gpt.moonshot.response;

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
