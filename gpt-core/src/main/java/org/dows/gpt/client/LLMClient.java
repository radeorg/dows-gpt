package org.dows.gpt.client;

import org.springframework.web.socket.WebSocketSession;

/**
 * 通用 LLM 接口定义
 */
public interface LLMClient {

    String getType();

    String chat(String sysPrompt, String userPrompt);

    void streamChat(String sysPrompt, String userPrompt, WebSocketSession session);

    boolean supports(String modelType);

    default String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

}
