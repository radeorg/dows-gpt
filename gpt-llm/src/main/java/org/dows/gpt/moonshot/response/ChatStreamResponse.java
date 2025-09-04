package org.dows.gpt.moonshot.response;

import lombok.Data;

import java.util.List;

/**
 * stream流式返回
 *
 */
@Data
public class ChatStreamResponse {

    private String id;

    private String object;

    /**
     * 时间戳
     */
    private long created;

    /**
     * 使用模型
     */
    private String model;

    /**
     * 返回对话内容
     */
    private List<ChatStreamChoice> choices;

}
