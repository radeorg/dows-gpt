package org.dows.gpt.internlm.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 使用token
 *
 */
@Data
public class ChatTokenData {

    /**
     * 使用token
     */
    @SerializedName("total_tokens")
    public int totalTokens;


}
