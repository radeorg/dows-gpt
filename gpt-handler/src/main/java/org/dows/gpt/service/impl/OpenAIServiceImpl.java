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
import org.dows.gpt.llm.openai.OpenAiClient;
import org.dows.gpt.llm.openai.entity.chat.ChatChoice;
import org.dows.gpt.llm.openai.entity.chat.ChatCompletion;
import org.dows.gpt.llm.openai.entity.chat.ChatCompletionResponse;
import org.dows.gpt.llm.openai.entity.chat.Message;
import org.dows.gpt.llm.openai.listener.SSEListener;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * openai 接口实现
 *
 */
@Service
public class OpenAIServiceImpl implements ModelService {
    private static OpenAiClient openAiClient;

    public OpenAIServiceImpl(OpenAiClient openAiClient) {
        OpenAIServiceImpl.openAiClient = openAiClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ValidatorUtil.isNull(openAiClient)) {
            throw new BusinessException("ChatGpt无有效token，请切换其他模型进行聊天");
        }
        List<Message> openAiMessages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            Message currentMessage = Message.builder().content(v.getContent()).role(v.getRole()).build();
            openAiMessages.add(currentMessage);
        });
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(openAiMessages)
                .model(ValidatorUtil.isNotNull(version) ? version : ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        ChatCompletionResponse response;
        try {
            response = openAiClient.chatCompletion(chatCompletion);
        } catch (Exception e) {
            throw new LLMException("OpenAi接口请求异常，请稍后再试");
        }
        ChatChoice choice = response.getChoices().get(0);
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getId())
                .model(ChatModelEnum.OPENAI.getValue()).modelVersion(response.getModel())
                .content(choice.getMessage().getContent()).role(choice.getMessage().getRole()).finishReason(choice.getFinishReason())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(openAiClient.getApiKey().get(0)).usedTokens(response.getUsage().getTotalTokens())
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
                              Long chatId, String conversationId, String prompt, String version, String uid) {
        if (ValidatorUtil.isNullIncludeArray(openAiClient.getApiKey())) {
            throw new BusinessException("未加载到密钥信息");
        }
        List<Message> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            Message currentMessage = Message.builder().content(v.getContent()).role(v.getRole()).build();
            messages.add(currentMessage);
        });
        SSEListener sseListener = new SSEListener(response, sseEmitter, chatId, conversationId, ChatModelEnum.OPENAI.getValue(), version, uid, isWs);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ValidatorUtil.isNotNull(version) ? version : ChatCompletion.Model.GPT_3_5_TURBO_0613.getName())
                .build();
        openAiClient.streamChatCompletion(completion, sseListener);
        if (isWs) {
            return false;
        }
        sseListener.getCountDownLatch().await();
        return sseListener.getError();
    }

}
