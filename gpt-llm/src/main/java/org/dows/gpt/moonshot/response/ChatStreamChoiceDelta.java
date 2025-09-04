package org.dows.gpt.moonshot.response;

import lombok.Data;

/**
 * 返回内容
 *
 */
@Data
public class ChatStreamChoiceDelta {

    /**
     * 内容
     */
    private String content;

    /**
     * 角色
     */
    private String role;

}
