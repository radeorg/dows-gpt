package org.dows.gpt.internlm.response;

import lombok.Data;

/**
 * 返回内容
 *
 */
@Data
public class ChatStreamChoiceDelta {

    /**
     * 角色
     */
    private String role;

    /**
     * 内容
     */
    private String content;

}
