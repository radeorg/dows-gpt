package org.dows.gpt.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LLMClientFactory {

    private final Map<String, LLMClient> modelClientMap;

    public LLMClientFactory(List<LLMClient> clients) {
        modelClientMap = new HashMap<>();
        for (LLMClient client : clients) {
            modelClientMap.put(client.getType(), client); // 例如 "gpt-4o", "claude", "deepseek"
        }
    }

    public LLMClient getClient(String type) {
        return modelClientMap.get(type);
    }
}
