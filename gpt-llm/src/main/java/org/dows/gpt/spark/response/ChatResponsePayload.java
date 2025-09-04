package org.dows.gpt.spark.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 返回体
 *
 */
@Data
public class ChatResponsePayload implements Serializable {
    private static final long serialVersionUID = 8090192271782303700L;

    /**
     * 返回内容
     */
    private ChatResponseChoices choices;

    /**
     * 使用信息
     */
    private ChatUsage usage;

}
