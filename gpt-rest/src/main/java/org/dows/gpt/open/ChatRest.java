package org.dows.gpt.open;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.gpt.api.ChatApi;
import org.dows.gpt.client.DeepSeekR1Client;
import org.dows.gpt.request.ChatRequest;
import org.dows.gpt.response.GptLockResponse;
import org.dows.gpt.service.ChatService;
import org.dows.gpt.sse.SseClient;
import org.dows.gpt.token.JwtTokenProvider;
import org.dows.llm.deepseek.DeepSeekStreamingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Slf4j
@RestController
public class ChatRest implements ChatApi {

    private final ChatService chatService;

    private final JwtTokenProvider jwtTokenProvider;

    private final DeepSeekR1Client deepSeekR1Client;

    private final SseClient sseClient;

    private final DeepSeekStreamingService deepSeekService;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private final Scheduler bizScheduler = Schedulers.boundedElastic();
    @GetMapping("/v1/api/ai/chat")
    public ModelAndView chat(ModelAndView modelAndView, @RequestParam(value = "modelType", defaultValue = "deepseek") String modelType) {
        // 默认模型类型
        modelAndView.addObject("modelType", modelType);
        // 在 controller 里处理
        modelAndView.setViewName("ftl/chat");
        return modelAndView;
    }

    //@PostMapping("/chat")
//    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
//        return ResponseEntity.ok(chatService.chat(request));
//    }
//
//    public ResponseEntity<Integer> getLocked(@RequestParam String appId){
//        return ResponseEntity.ok(chatService.getLocked(appId));
//
//    }
//
//    public ResponseEntity<GptLockResponse> statistics(@RequestParam String appId){
//        return ResponseEntity.ok(chatService.statistics(appId));
//    }

    @Override
    public ResponseEntity<String> chat(ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(request));
    }

    //@GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String prompt) {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            // SEE 流式回复
            deepSeekR1Client.streamChatSEE(prompt, emitter);
            emitter.complete();
        });
        return emitter;
    }

    @Override
    public ResponseEntity<Integer> getLocked(String appId) {
        return null;
    }

    @Override
    public ResponseEntity<GptLockResponse> statistics(String appId) {
        return null;
    }



    /**
     * SSE接口：接收客户端提问，流式推送DeepSeek的回复
     * 每个客户端请求会创建独立的SSE连接，数据隔离
     * @param request 客户端的提问内容
     * @return SSE流式响应
     */
    @GetMapping(value = "/sse/deepseek", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam ChatRequest request) {

        return deepSeekService.callDeepSeekStreaming(request)
                .filter(content -> !content.isEmpty())
                // 若有耗时的同步操作（如数据处理），切换到专用调度器，避免阻塞事件循环线程
                .publishOn(bizScheduler)
                .map(content -> ServerSentEvent.<String>builder()
                        .id(UUID.randomUUID().toString())
                        .event("deepseek-reply")
                        .data(content)
                        .build())
                .onErrorResume(e -> {
                    String errorMsg = "调用DeepSeek失败：" + e.getMessage();
                    return Flux.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data(errorMsg)
                            .build());
                })
                .concatWith(Mono.just(ServerSentEvent.<String>builder()
                        .event("complete")
                        .data("回复完成")
                        .build()))
                .timeout(Duration.ofMinutes(5))
                // 取消订阅时释放资源（客户端断开连接时）
                .doOnCancel(() -> System.out.println("客户端主动断开连接"));
    }


}
