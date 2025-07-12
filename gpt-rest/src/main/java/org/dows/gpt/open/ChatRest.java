package org.dows.gpt.open;

import org.dows.gpt.client.DeepSeekR1Client;
import org.dows.gpt.request.ChatRequest;
import org.dows.gpt.service.ChatService;
import org.dows.gpt.session.SessionUser;
import org.dows.gpt.token.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/api/ai")
public class ChatRest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final DeepSeekR1Client deepSeekR1Client;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public ChatRest(@Qualifier("deepSeekR1Client") DeepSeekR1Client deepSeekR1Client) {
        this.deepSeekR1Client = deepSeekR1Client;
    }

    @GetMapping("/chat")
    public ModelAndView chat(ModelAndView modelAndView, @RequestParam(value = "modelType", defaultValue = "deepseek") String modelType) {
        // 默认模型类型
        modelAndView.addObject("modelType", modelType);
        // 在 controller 里处理
        modelAndView.setViewName("ftl/chat");
        return modelAndView;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request, @RequestHeader("Authorization") String token) {
        // 认证中心解析 JWT 验证权限
        SessionUser sessionUser = jwtTokenProvider.validateUserToken(token);
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.ok(chatService.chat(request));
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String prompt) {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            // SEE 流式回复
            deepSeekR1Client.streamChatSEE(prompt, emitter);
            emitter.complete();
        });
        return emitter;
    }
}
