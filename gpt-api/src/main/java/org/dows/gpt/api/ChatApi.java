package org.dows.gpt.api;

import org.dows.gpt.request.ChatRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatApi {

    @PostMapping("/v1/open/gpt/chat")
    ResponseEntity<String> chat(@RequestBody ChatRequest request);

    @GetMapping(value = "/v1/open/gpt/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter streamChat(@RequestParam String prompt);

    @GetMapping(value = "/v1/open/gpt/chat/locked", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    ResponseEntity<Integer> getLocked(@RequestParam String appId);

//    @PostMapping("/v1/open/gpt/count")
//    ResponseEntity<String> count(@RequestBody ChatRequest request);


}
