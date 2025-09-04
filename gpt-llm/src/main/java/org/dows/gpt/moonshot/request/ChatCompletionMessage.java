package org.dows.gpt.moonshot.request;

import lombok.Data;

/**
 * 对话内容
 *
 */
@Data
public class ChatCompletionMessage {

    /**
     * 角色
     */
    public String role;
    public String name;
    public String content;
    public Boolean partial;

    public ChatCompletionMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public ChatCompletionMessage(String role, String name, String content, Boolean partial) {
        this.role = role;
        this.name = name;
        this.content = content;
        this.partial = partial;
    }

}
