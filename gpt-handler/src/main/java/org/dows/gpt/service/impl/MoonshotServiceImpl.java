package org.dows.gpt.service.impl;

import org.dows.gpt.client.enums.ChatRoleEnum;
import org.dows.gpt.client.model.command.ChatMessageCommand;
import org.dows.gpt.client.model.dto.ChatMessageDTO;
import org.dows.gpt.common.exception.BusinessException;
import org.dows.gpt.framework.validator.ValidatorUtil;
import org.dows.gpt.service.ModelService;
import org.dows.gpt.moonshot.MoonshotClient;
import org.dows.gpt.moonshot.constant.ModelConstant;
import org.dows.gpt.moonshot.request.ChatCompletion;
import org.dows.gpt.moonshot.request.ChatCompletionMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 月之暗面 接口实现
 *
 */
@Service
public class MoonshotServiceImpl implements ModelService {
    private static MoonshotClient moonshotClient;

    @Autowired
    public MoonshotServiceImpl(MoonshotClient moonshotClient) {
        MoonshotServiceImpl.moonshotClient = moonshotClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        return null;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
                              Long chatId, String conversationId, String prompt, String version, String uid) {
        if (ValidatorUtil.isNull(moonshotClient.getApiKey())) {
            throw new BusinessException("未加载到密钥信息");
        }
        List<ChatCompletionMessage> messages = new ArrayList<>();
        chatMessages.stream().filter(d -> !d.getRole().equals(ChatRoleEnum.SYSTEM.getValue())).forEach(v -> {
            messages.add(new ChatCompletionMessage(v.getRole(), v.getContent()));
        });
        String modelVaersion = ValidatorUtil.isNotNull(version) ? version : ModelConstant.API_MODEL_8K;
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(modelVaersion)
                .messages(messages)
                .build();
        return moonshotClient.streamChat(response, chatCompletion, chatId, conversationId, modelVaersion, uid, isWs);
    }

}
