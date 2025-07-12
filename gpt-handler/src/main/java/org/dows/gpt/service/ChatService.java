package org.dows.gpt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.gpt.client.LLMClient;
import org.dows.gpt.request.ChatMessage;
import org.dows.gpt.request.ChatRequest;
import org.dows.gpt.request.ChatSession;
import org.dows.gpt.utils.ChatTokenUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final List<LLMClient> llmClients;

    private final ChatSessionService chatSessionService;

    private final ChatMessageService chatMessageService;

    public String chat(ChatRequest request) {
        String message = request.getMessage();
        String modelType = request.getModelType();
        ChatSession session = getOrCreateSession(request);
        List<ChatMessage> history = chatMessageService.findLastMessages(session.getId(), 10);
        String prompt = buildPrompt(history, message);
        LLMClient client = llmClients.stream()
                .filter(c -> c.supports(modelType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("模型不支持"));

        String answer = client.chat(prompt);

        int inputTokens = ChatTokenUtil.count(prompt);
        int outputTokens = ChatTokenUtil.count(answer);

        //todo 保留QA
        //chatMessageService.insert(new ChatMessageEntity("", "", prompt, "user", inputTokens, modelType));
        //chatMessageService.insert(new ChatMessageEntity("", "", answer, "assistant", outputTokens, modelType));

        return answer;
    }

    private String buildPrompt(List<ChatMessage> history, String newPrompt) {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : history) {
            sb.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }
        // TODO 历史会话拼接
        sb.append("user: ").append(newPrompt);
        return sb.toString();
    }

    private ChatSession getOrCreateSession(ChatRequest request) {
        if (request.getSessionId() != null) {
            return chatSessionService.findById(request.getSessionId());
        }
        ChatSession session = new ChatSession();
        session.setUserId(request.getUserId());
        session.setModelType(request.getModelType());
        chatSessionService.insert(session);
        return session;
    }
}
