package org.dows.gpt.service.impl;

import com.alibaba.fastjson.JSON;
import org.dows.gpt.client.enums.ChatModelEnum;
import org.dows.gpt.client.enums.ChatRoleEnum;
import org.dows.gpt.client.enums.ChatStatusEnum;
import org.dows.gpt.client.model.command.ChatMessageCommand;
import org.dows.gpt.client.model.dto.ChatMessageDTO;
import org.dows.gpt.common.exception.BusinessException;
import org.dows.gpt.framework.validator.ValidatorUtil;
import org.dows.gpt.service.ModelService;
import org.dows.gpt.doubao.DouBaoClient;
import org.dows.gpt.doubao.enums.ModelEnum;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 豆包 接口实现
 *
 */
@Service
public class DouBaoServiceImpl implements ModelService {
    private static DouBaoClient douBaoClient;

    @Autowired
    public DouBaoServiceImpl(DouBaoClient douBaoClient) {
        DouBaoServiceImpl.douBaoClient = douBaoClient;
    }

    /**
     * 豆包文本对话
     * https://www.volcengine.com/docs/82379/1298454
     *
     * @param chatId
     * @param chatMessages
     * @return
     */
    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        if (ValidatorUtil.isNull(douBaoClient)) {
            throw new BusinessException("未加载到豆包密钥信息");
        }
        List<ChatMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            messages.add(ChatMessage.builder().role(ChatMessageRole.valueOf(v.getRole().toUpperCase())).content(v.getContent()).build());
        });
        String modelVaersion = ValidatorUtil.isNotNull(version) ? version : ModelEnum.LITE.getModel();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        ChatCompletionResult response = douBaoClient.chat(request);
        ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(response.getId())
                .model(ChatModelEnum.DOUBAO.getValue()).modelVersion(modelVaersion)
                .content(response.getChoices().get(0).getMessage().getContent().toString()).role(ChatRoleEnum.ASSISTANT.getValue())
                .status(ChatStatusEnum.SUCCESS.getValue()).appKey(douBaoClient.getApiKey()).usedTokens(Long.valueOf(response.getUsage().getTotalTokens()))
                .response(JSON.toJSONString(response))
                .build();
        return chatMessage;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
                              Long chatId, String conversationId, String prompt, String version, String uid) {
        if (ValidatorUtil.isNull(douBaoClient.getApiKey())) {
            throw new BusinessException("未加载到豆包密钥信息");
        }
        List<ChatMessage> messages = new ArrayList<>();
        chatMessages.stream().forEach(v -> {
            messages.add(ChatMessage.builder().role(ChatMessageRole.valueOf(v.getRole().toUpperCase())).content(v.getContent()).build());
        });
        String modelVaersion = ValidatorUtil.isNotNull(version) ? version : ModelEnum.LITE.getModel();
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        Boolean flag = douBaoClient.streamChat(response, chatCompletionRequest, chatId, conversationId, modelVaersion, uid, isWs);
        if (isWs) {
            return false;
        }
        return flag;
    }

}
