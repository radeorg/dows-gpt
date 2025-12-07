package org.dows.gpt.api;

import org.dows.gpt.request.ChatRequest;
import org.dows.gpt.response.GptLockResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

public interface ChatApi {

    @PostMapping("/v1/open/gpt/chat")
    ResponseEntity<String> chat(@RequestBody ChatRequest request);

    @GetMapping(value = "/v1/open/gpt/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter streamChat(@RequestParam String prompt);

    @GetMapping(value = "/v1/open/gpt/chat/locked", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    ResponseEntity<Integer> getLocked(@RequestParam String appId);

    @GetMapping(value = "/v1/open/gpt/chat/statistics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    ResponseEntity<GptLockResponse> statistics(@RequestParam String appId);

    //    @PostMapping("/v1/open/gpt/count")
//    ResponseEntity<String> count(@RequestBody ChatRequest request);
    @PostMapping(value = "/v1/open/llm/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request);

}
