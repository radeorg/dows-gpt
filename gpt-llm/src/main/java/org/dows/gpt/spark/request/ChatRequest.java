package org.dows.gpt.spark.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dows.gpt.spark.enums.ModelEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火请求信息
 *
 */
@Data
public class ChatRequest implements Serializable {
    private static final long serialVersionUID = 8142547165395379456L;

    private ChatHeader header;

    private ChatParameter parameter;

    private ChatPayload payload;

    @JsonIgnore
    private transient ModelEnum apiVersion = ModelEnum.Lite;

    public static ChatRequestBuilder builder() {
        return new ChatRequestBuilder();
    }

}
