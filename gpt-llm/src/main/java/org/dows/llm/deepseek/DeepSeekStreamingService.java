package org.dows.llm.deepseek;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.gpt.request.ChatRequest;
import org.dows.gpt.yml.LlmClientConfig;
import org.dows.gpt.yml.LlmClientsProperties;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeepSeekStreamingService {
    private WebClient webClient;
    private final ObjectMapper objectMapper;
    private final LlmClientsProperties llmClientsProperties;

    @PostConstruct
    public void init() {
        //LlmClientsProperties lmClientsProperties = llmClientsProperties;
        LlmClientConfig deepSeekConfig = llmClientsProperties.getClients().get("deepseek-v3");
        // 构建响应式WebClient
        this.webClient = WebClient.builder()
                .defaultHeader("Accept-Charset", "UTF-8")
                .baseUrl(deepSeekConfig.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                .build();
    }

    /**
     * 流式调用DeepSeek API，返回逐块的回复内容
     * @param request 客户端的提问内容
     * @return 流式的回复文本（Flux<String>）
     */
    public Flux<String> callDeepSeekStreaming(ChatRequest request) {
        // 构建DeepSeek的请求体（开启流式返回）
        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-chat", // DeepSeek模型名（根据实际使用的模型调整）
                "messages", List.of(
                        Map.of("role", "user", "content", request.getPrompt())
                ),
                "stream", true, //开启流式返回
                "temperature", 0.7
        );

        // 流式调用DeepSeek API，解析每行响应的内容
        return webClient.post()
                .body(Mono.just(requestBody), Map.class)
                // 关键：以文本流的方式接收响应（text/event-stream格式）
                .header("Accept-Charset", "UTF-8")
                .retrieve()
                .bodyToFlux(String.class)
                // 关键修改：使用DataBuffer而非String
//                .bodyToFlux(DataBuffer.class)
//                // 手动转换DataBuffer为UTF-8字符串
//                .map(dataBuffer -> {
//                    try {
//                        // 使用UTF-8明确编码
//                        return StreamUtils.copyToString(dataBuffer.asInputStream(), java.nio.charset.StandardCharsets.UTF_8);
//                    } catch (IOException e) {
//                        log.error("数据转换失败", e);
//                        return "";
//                    } finally {
//                        // 释放DataBuffer资源
//                        DataBufferUtils.release(dataBuffer);
//                    }
//                })
//                // 按行分割流（解决可能的多行合并问题）
//                .flatMap(line -> Flux.fromArray(line.split("\\n")))
                // 过滤空行和结束标记（[DONE]）
                .filter(line -> !line.isEmpty() && !line.equals("[DONE]"))
                // 解析每行JSON，提取回复内容
                .map(this::parseDeepSeekStreamResponse);
    }

    /**
     * 解析DeepSeek流式响应的单行数据，提取实际回复内容
     * DeepSeek流式响应格式：data: {"choices":[{"delta":{"content":"你"}}]}
     */
    private String parseDeepSeekStreamResponse(String line) {
        try {
            // 去掉前缀 "data: "，获取纯JSON字符串
            String jsonStr = line.startsWith("data: ") ? line.substring(6) : line;
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            JsonNode contentNode = rootNode.path("choices")
                    .path(0)
                    .path("delta")
                    .path("content");
            // 返回非空的内容（避免空字符串推送）
            return contentNode.isMissingNode() ? "" : contentNode.asText();
        } catch (Exception e) {
            // 解析失败时返回空字符串，避免中断流式推送
            return "";
        }
    }
}