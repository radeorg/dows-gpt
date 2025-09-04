package org.dows.gpt.service.impl;

import org.dows.gpt.client.enums.ChatModelEnum;
import org.dows.gpt.client.model.command.ChatMessageCommand;
import org.dows.gpt.client.model.dto.ChatMessageDTO;
import org.dows.gpt.client.model.dto.ModelDTO;
import org.dows.gpt.client.service.GptService;
import org.dows.gpt.common.exception.BusinessException;
import org.dows.gpt.service.ModelService;
import org.dows.gpt.llm.locallm.base.enums.ModelTypeEnum;
import org.dows.gpt.llm.locallm.coze.CozeClient;
import org.dows.gpt.llm.locallm.dify.DifyClient;
import org.dows.gpt.llm.locallm.gitee.GiteeClient;
import org.dows.gpt.llm.locallm.langchain.LangchainClient;
import org.dows.gpt.llm.locallm.ollama.OllamaClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 本地模型 接口实现
 *
 */
@Service
public class LocalLMServiceImpl implements ModelService {
    private static LangchainClient langchainClient;
    private static OllamaClient ollamaClient;
    private static CozeClient cozeClient;
    private static GptService gptService;
    private static GiteeClient giteeClient;
    private static DifyClient difyClient;

    @Autowired
    public LocalLMServiceImpl(GptService gptService, LangchainClient langchainClient, OllamaClient ollamaClient, CozeClient cozeClient,
                              GiteeClient giteeClient, DifyClient difyClient) {
        LocalLMServiceImpl.langchainClient = langchainClient;
        LocalLMServiceImpl.ollamaClient = ollamaClient;
        LocalLMServiceImpl.cozeClient = cozeClient;
        LocalLMServiceImpl.gptService = gptService;
        LocalLMServiceImpl.giteeClient = giteeClient;
        LocalLMServiceImpl.difyClient = difyClient;
    }

    @Override
    public ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version) {
        return null;
    }

    @Override
    @SneakyThrows
    public Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
                              Long chatId, String conversationId, String prompt, String version, String uid) {
        ModelDTO modelDTO = gptService.getModel(ChatModelEnum.LOCALLM.getValue());
        ModelTypeEnum modelType = ModelTypeEnum.getEnum(modelDTO.getLocalModelType());
        switch (modelType) {
            case LANGCHAIN:
                return langchainClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid, chatMessages, prompt, version, modelDTO);
            case OLLAMA:
                return ollamaClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid, chatMessages, prompt, version, modelDTO);
            case COZE:
                return cozeClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid, chatMessages, prompt, version, modelDTO);
            case GITEE_AI:
                return giteeClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid, chatMessages, prompt, version, modelDTO);
            case Dify:
                return difyClient.buildChatCompletion(response, sseEmitter, chatId, conversationId, isWs, uid, chatMessages, prompt, version, modelDTO);
            default:
                throw new BusinessException("未知的模型类型，功能未接入");
        }
    }


}
