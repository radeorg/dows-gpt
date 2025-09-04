package org.dows.gpt.internlm.response;

import com.google.gson.annotations.SerializedName;
import org.dows.gpt.internlm.request.ChatCompletionMessage;
import lombok.Data;

/**
 * 返回内容
 *
 */
@Data
public class ChatChoice {


    private int index;

    /**
     * 对话内容
     */
    private ChatCompletionMessage message;

    /**
     * 结束原因
     */
    @SerializedName("finish_reason")
    private String finishReason;

}
