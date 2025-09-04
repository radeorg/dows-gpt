package org.dows.gpt.spark.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 响应
 *
 */
@Data
public class ChatResponse implements Serializable {
    private static final long serialVersionUID = 886720558849587945L;

    private ChatResponseHeader header;

    private ChatResponsePayload payload;

}
