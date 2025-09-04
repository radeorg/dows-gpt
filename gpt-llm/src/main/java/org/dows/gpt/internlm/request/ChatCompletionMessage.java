package org.dows.gpt.internlm.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话内容
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionMessage {

    /**
     * 角色
     */
    public String role;

    /**
     * 内容
     */
    public String text;

}
