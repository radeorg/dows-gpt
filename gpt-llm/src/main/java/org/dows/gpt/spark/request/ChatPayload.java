package org.dows.gpt.spark.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火请求体
 *
 */
@Data
public class ChatPayload implements Serializable {
    private static final long serialVersionUID = 2084163918219863102L;

    /**
     * 消息内容
     */
    private ChatMessage message;

    public ChatPayload() {
    }

    public ChatPayload(ChatMessage message) {
        this.message = message;
    }

}
