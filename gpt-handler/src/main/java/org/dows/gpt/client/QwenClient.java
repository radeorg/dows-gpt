package org.dows.gpt.client;

import org.springframework.web.socket.WebSocketSession;

public class QwenClient implements LLMClient{
    @Override
    public String getType() {
        return "";
    }

    @Override
    public String chat(String prompt, String content) {
        return "";
    }

    @Override
    public void streamChat(String prompt, WebSocketSession session) {

    }

    @Override
    public boolean supports(String modelType) {
        return false;
    }
}
