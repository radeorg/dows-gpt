package org.dows.gpt.wenxin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 使用量
 *
 */
@Data
public class Usage {
    @JsonProperty("prompt_tokens")
    private long promptTokens;
    @JsonProperty("completion_tokens")
    private long completionTokens;
    @JsonProperty("total_tokens")
    private long totalTokens;
}
