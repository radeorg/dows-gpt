package org.dows.gpt.internlm.response;

import lombok.Data;

import java.util.List;

/**
 * 输出返回
 *
 */
@Data
public class ChatResponse {

    private String id;

    private String object;

    private long created;

    /**
     * 模型
     */
    private String model;

    /**
     * 对话内容
     */
    private List<ChatChoice> choices;

    /**
     * 使用量
     */
    private Usage usage;

    /**
     * 使用量
     */
    private ChatTokenData data;

}
