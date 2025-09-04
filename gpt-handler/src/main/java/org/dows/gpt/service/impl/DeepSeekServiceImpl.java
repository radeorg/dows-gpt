package org.dows.gpt.service.impl;

import com.alibaba.fastjson.JSON;
import org.dows.gpt.client.enums.ChatModelEnum;
import org.dows.gpt.client.enums.ChatStatusEnum;
import org.dows.gpt.client.model.command.ChatMessageCommand;
import org.dows.gpt.client.model.dto.ChatMessageDTO;
import org.dows.gpt.common.exception.BusinessException;
import org.dows.gpt.framework.validator.ValidatorUtil;
import org.dows.gpt.llm.LLMException;
import org.dows.gpt.service.ModelService;
import org.dows.gpt.deepseek.DeepSeekClient;
import org.dows.gpt.deepseek.enmus.Model;
import org.dows.gpt.deepseek.sse.SSEListener;
import org.dows.gpt.llm.openai.entity.chat.ChatChoice;
import org.dows.gpt.llm.openai.entity.chat.ChatCompletion;
import org.dows.gpt.llm.openai.entity.chat.ChatCompletionResponse;
import org.dows.gpt.llm.openai.entity.chat.Message;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * DeepSeek 接口实现
 *
 */
@Service
public class DeepSeekServiceImpl implements ModelService {
    private static DeepSeekClient deepSeekClient;

    public DeepSeekServiceImpl(DeepSeekClient deepSeekClient) {
        DeepSeekServiceImpl.deepSeekClient = deepSeekClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ValidatorUtil.isNull(deepSeekClient)) {
            throw new BusinessException("DeepSeek无有效token，请切换其他模型进行聊天");
        }
        List<Message> openAiMessages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            Message currentMessage = Message.builder().content(v.getContent()).role(v.getRole()).build();
            openAiMessages.add(currentMessage);
        });
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(openAiMessages)
                .model(ValidatorUtil.isNotNull(version) ? version : Model.CHAT.getName())
                .build();
        ChatCompletionResponse response;
        try {
            response = deepSeekClient.chatCompletion(chatCompletion);
        } catch (Exception e) {
            throw new LLMException("DeepSeek接口请求异常，请稍后再试");
        }
        ChatChoice choice = response.getChoices().get(0);
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getId())
                .model(ChatModelEnum.DEEPSEEK.getValue()).modelVersion(response.getModel())
                .content(choice.getMessage().getContent()).role(choice.getMessage().getRole()).finishReason(choice.getFinishReason())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(deepSeekClient.getApiKey().get(0)).usedTokens(response.getUsage().getTotalTokens())
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
                              Long chatId, String conversationId, String prompt, String version, String uid) {
        if (ValidatorUtil.isNullIncludeArray(deepSeekClient.getApiKey())) {
            throw new BusinessException("未加载到密钥信息");
        }
        List<Message> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            Message currentMessage = Message.builder().content(v.getContent()).role(v.getRole()).build();
            messages.add(currentMessage);
        });
        SSEListener sseListener = new SSEListener(response, sseEmitter, chatId, conversationId, ChatModelEnum.DEEPSEEK.getValue(), version, uid, isWs);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ValidatorUtil.isNotNull(version) ? version : Model.CHAT.getName())
                .build();
        deepSeekClient.streamChatCompletion(completion, sseListener);
        if (isWs) {
            return false;
        }
        sseListener.getCountDownLatch().await();
        return sseListener.getError();
    }

}
