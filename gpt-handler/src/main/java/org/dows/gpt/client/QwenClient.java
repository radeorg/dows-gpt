package org.dows.gpt.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.gpt.enums.ModelTypeEnum;
import org.dows.gpt.yml.LlmClientConfig;
import org.dows.gpt.yml.LlmClientsProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QwenClient implements LLMClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LlmClientsProperties clientsProperties;

    @Override
    public String getType() {
        return ModelTypeEnum.QWEN.getCode();
    }

    @Override
    public String chat(String sysPrompt, String userPrompt) {
        LlmClientConfig config = clientsProperties.getClients().get(getType());
        if (config == null || !config.getEnabled()) {
            log.error("Qwen client is not configured or disabled");
            return "Qwen client is not available";
        }

        HttpHeaders headers = buildHeaders(config);
        Map<String, Object> body = buildBody(config, sysPrompt, userPrompt);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getApiUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return extractContent(response.getBody());
        } catch (Exception e) {
            log.error("Error calling Qwen API: {}", e.getMessage(), e);
            return "Error processing your request";
        }
    }

    @Override
    public void streamChat(String sysPrompt, String userPrompt, WebSocketSession session) {
        LlmClientConfig config = clientsProperties.getClients().get(getType());
        if (config == null || !config.getEnabled()) {
            log.error("Qwen client is not configured or disabled");
            if (session.isOpen()) {
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }

        HttpHeaders headers = buildHeaders(config);
        Map<String, Object> body = buildBody(config, sysPrompt, userPrompt);
        body.put("stream", true);

        RequestCallback requestCallback = request -> {
            objectMapper.writeValue(request.getBody(), body);
            request.getHeaders().addAll(headers);
        };

        try {
            restTemplate.execute(
                    config.getApiUrl(),
                    HttpMethod.POST,
                    requestCallback,
                    response -> {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("data: ")) {
                                    String json = line.substring(6).trim();
                                    if (!json.equals("[DONE]")) {
                                        String content = extractContentStream(json);
                                        if (content != null && !content.isEmpty() && session.isOpen()) {
                                            Map<String, String> wsMsg = new HashMap<>();
                                            wsMsg.put("type", "CONTENT");
                                            wsMsg.put("data", content);
                                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMsg)));
                                        }
                                    } else {
                                        if (session.isOpen()) {
                                            Map<String, String> doneMsg = new HashMap<>();
                                            doneMsg.put("type", "DONE");
                                            doneMsg.put("data", "");
                                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(doneMsg)));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("Error processing Qwen stream: {}", e.getMessage(), e);
                            if (session.isOpen()) {
                                session.close(CloseStatus.SERVER_ERROR);
                            }
                        }
                        return null;
                    }
            );
        } catch (Exception e) {
            log.error("Error calling Qwen stream API: {}", e.getMessage(), e);
            if (session.isOpen()) {
                try {
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Override
    public boolean supports(String modelType) {
        return ModelTypeEnum.QWEN.getCode().equals(modelType);
    }

    private HttpHeaders buildHeaders(LlmClientConfig config) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + config.getApiKey());
        return headers;
    }

    private Map<String, Object> buildBody(LlmClientConfig config, String sysPrompt, String userPrompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("temperature", 0.7);
        body.put("max_tokens", 8192);

        List<Map<String, String>> messages = new ArrayList<>();
        if (sysPrompt != null && !sysPrompt.isEmpty()) {
            messages.add(new HashMap<>() {{
                put("role", "system");
                put("content", sysPrompt);
            }});
        }
        messages.add(new HashMap<>() {{
            put("role", "user");
            put("content", userPrompt);
        }});
        body.put("messages", messages);
        return body;
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode choice = root.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
            }
            return "No valid response from Qwen API";
        } catch (Exception e) {
            log.error("Error parsing Qwen response: {}", e.getMessage(), e);
            return "Error processing response";
        }
    }

    private String extractContentStream(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode choice = root.get("choices").get(0);
                if (choice.has("delta") && choice.get("delta").has("content")) {
                    return choice.get("delta").get("content").asText();
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error parsing Qwen stream response: {}", e.getMessage(), e);
            return null;
        }
    }
}
