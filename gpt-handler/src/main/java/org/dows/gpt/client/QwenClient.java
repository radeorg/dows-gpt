package org.dows.gpt.client;

import org.springframework.web.socket.WebSocketSession;

public class QwenClient implements LLMClient{
    @Override
    public String getType() {
        return "";
    }

    @Override
    public String chat(String sysPrompt, String userPrompt) {
        return "";
    }

    @Override
    public void streamChat(String sysPrompt, String userPrompt, WebSocketSession session) {

    }

    @Override
    public boolean supports(String modelType) {
        return false;
    }
}
