package org.dows.gpt.internlm.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对话请求
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletion {

    /***
     * 模型
     */
    public String model;

    /**
     * 消息
     */
    public List<ChatCompletionMessage> messages;

    @SerializedName("temperature")
    public float temperature;

    @SerializedName("top_p")
    public float topP;

    @SerializedName("request_output_len")
    public Integer requestOutputLen;

    public boolean stream = false;

}
