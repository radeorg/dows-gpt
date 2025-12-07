package org.dows.gpt.service;//package org.dows.gpt.service;
//
////import org.dows.gpt.base.service.impl.*;
////import org.dows.gpt.client.enums.ChatModelEnum;
////import org.dows.gpt.client.enums.ChatStatusEnum;
////import org.dows.gpt.client.model.command.ChatCommand;
////import org.dows.gpt.client.model.command.ChatMessageCommand;
////import org.dows.gpt.client.model.dto.ChatMessageDTO;
////import org.dows.gpt.client.service.GptService;
////import org.dows.gpt.common.api.ResponseInfo;
////import org.dows.gpt.common.enums.IntegerEnum;
////import org.dows.gpt.common.exception.BusinessException;
////import org.dows.gpt.common.exception.ErrorException;
////import org.dows.gpt.common.utils.DozerUtil;
////import org.dows.gpt.framework.validator.ValidatorUtil;
////import org.dows.gpt.ChatData;
////import org.dows.gpt.llm.LLMException;
////import org.dows.gpt.llm.base.service.impl.*;
////import org.dows.gpt.chatglm.ChatGLMClient;
////import org.dows.gpt.deepseek.DeepSeekClient;
////import org.dows.gpt.doubao.DouBaoClient;
////import org.dows.gpt.internlm.InternlmClient;
////import org.dows.gpt.llm.locallm.coze.CozeClient;
////import org.dows.gpt.llm.locallm.dify.DifyClient;
////import org.dows.gpt.llm.locallm.gitee.GiteeClient;
////import org.dows.gpt.llm.locallm.langchain.LangchainClient;
////import org.dows.gpt.llm.locallm.ollama.OllamaClient;
////import org.dows.gpt.moonshot.MoonshotClient;
////import org.dows.gpt.llm.openai.OpenAiClient;
//import org.apache.xmlbeans.impl.validator.ValidatorUtil;
//import org.dows.gpt.ChatData;
//import org.dows.gpt.LLMException;
//import org.dows.gpt.chatglm.ChatGLMClient;
//import org.dows.gpt.deepseek.DeepSeekClient;
//import org.dows.gpt.doubao.DouBaoClient;
//import org.dows.gpt.internlm.InternlmClient;
//import org.dows.gpt.moonshot.MoonshotClient;
//import org.dows.gpt.spark.SparkClient;
//import org.dows.gpt.tongyi.TongYiClient;
//import org.dows.gpt.wenxin.WenXinClient;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.gpt.service.impl.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * 大模型 接口实现
// *
// */
//@Slf4j
//@Service
//public class LLMService {
//    private static final String[] drawingWords = {"画画", "作画", "画图", "绘画", "描绘"};
//    private static final String[] drawingInstructions = {"请画", "画一", "画个",};
//    private static OpenAiClient openAiClient;
//    private static DeepSeekClient deepSeekClient;
//    private static WenXinClient wenXinClient;
//    private static ChatGLMClient chatGLMClient;
//    private static TongYiClient tongYiClient;
//    private static SparkClient sparkClient;
//    private static MoonshotClient moonshotClient;
//    private static DouBaoClient douBaoClient;
//    private static InternlmClient internlmClient;
//    private static LangchainClient langchainClient;
//    private static OllamaClient ollamaClient;
//    private static CozeClient cozeClient;
//    private static GiteeClient giteeClient;
//    private static DifyClient difyClient;
//    private final GptService gptService;
//
//    @Autowired
//    public LLMService(GptService gptService, OpenAiClient openAiClient, DeepSeekClient deepSeekClient, WenXinClient wenXinClient,
//                      ChatGLMClient chatGLMClient, TongYiClient tongYiClient, SparkClient sparkClient, MoonshotClient moonshotClient, DouBaoClient douBaoClient,
//                      InternlmClient internlmClient, LangchainClient langchainClient, OllamaClient ollamaClient, CozeClient cozeClient, GiteeClient giteeClient,
//                      DifyClient difyClient) {
//        this.gptService = gptService;
//        LLMService.openAiClient = openAiClient;
//        LLMService.deepSeekClient = deepSeekClient;
//        LLMService.wenXinClient = wenXinClient;
//        LLMService.chatGLMClient = chatGLMClient;
//        LLMService.tongYiClient = tongYiClient;
//        LLMService.sparkClient = sparkClient;
//        LLMService.moonshotClient = moonshotClient;
//        LLMService.douBaoClient = douBaoClient;
//        LLMService.internlmClient = internlmClient;
//        LLMService.langchainClient = langchainClient;
//        LLMService.ollamaClient = ollamaClient;
//        LLMService.cozeClient = cozeClient;
//        LLMService.giteeClient = giteeClient;
//        LLMService.difyClient = difyClient;
//    }
//
//    public SseEmitter createSse(String uid) {
//        //默认30秒超时,设置为0L则永不超时
//        SseEmitter sseEmitter = new SseEmitter(0L);
//        //完成后回调
//        sseEmitter.onCompletion(() -> {
//            log.info("[{}]结束连接", uid);
//            LocalCache.CACHE.remove(uid);
//        });
//        //超时回调
//        sseEmitter.onTimeout(() -> {
//            log.info("[{}]连接超时", uid);
//        });
//        //异常回调
//        sseEmitter.onError(
//                throwable -> {
//                    try {
//                        log.info("[{}]连接异常,{}", uid, throwable.toString());
//                        sseEmitter.send(SseEmitter.event()
//                                .id(uid)
//                                .name("发生异常！")
//                                .data(ChatData.builder().content("发生异常请重试！").build())
//                                .reconnectTime(3000));
//                        LocalCache.CACHE.put(uid, sseEmitter);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//        );
//        try {
//            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        LocalCache.CACHE.put(uid, sseEmitter);
//        log.info("[{}]创建sse连接成功！", uid);
//        return sseEmitter;
//    }
//
//    public void closeSse(String uid) {
//        SseEmitter sse = (SseEmitter) LocalCache.CACHE.get(uid);
//        if (sse != null) {
//            sse.complete();
//            //移除
//            LocalCache.CACHE.remove(uid);
//        }
//    }
//
//    /**
//     * 根据模型获取大模型实现
//     *
//     * @param model
//     * @return
//     */
//    private ModelService getLLM(ChatModelEnum model) {
//        switch (model) {
//            case OPENAI:
//                return new OpenAIServiceImpl(openAiClient);
//            case DEEPSEEK:
//                return new DeepSeekServiceImpl(deepSeekClient);
//            case WENXIN:
//                return new WenXinServiceImpl(gptService, wenXinClient);
//            case TONGYI:
//                return new TongYiServiceImpl(tongYiClient);
//            case SPARK:
//                return new SparkServiceImpl(sparkClient);
//            case CHATGLM:
//                return new ChatGLMServiceImpl(chatGLMClient);
//            case INTERNLM:
//                return new InternLMServiceImpl(internlmClient);
//            case MOONSHOT:
//                return new MoonshotServiceImpl(moonshotClient);
//            case DOUBAO:
//                return new DouBaoServiceImpl(douBaoClient);
//            case LOCALLM:
//                return new LocalLMServiceImpl(gptService, langchainClient, ollamaClient, cozeClient, giteeClient, difyClient);
//            default:
//                return null;
//        }
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public ResponseInfo chat(ChatCommand command) {
//        command = gptService.validateGptCommand(command);
//        ChatMessageDTO userMessage = new ChatMessageDTO();
//        ChatMessageCommand chatMessage;
//        try {
//            Long chatId = gptService.saveChat(command);
//            List<ChatMessageDTO> messages = gptService.saveChatMessage(command, chatId, command.getConversationId());
//            userMessage = messages.get(messages.size() - 1);
//            if (ValidatorUtil.isNullIncludeArray(messages)) {
//                throw new BusinessException("消息发送失败");
//            }
//            ChatModelEnum modelEnum = ChatModelEnum.getEnum(command.getModel());
//            String version = command.getModelVersion();
//            chatMessage = getLLM(modelEnum).chat(messages, isDraw(command.getPrompt()), chatId, version);
//        } catch (LLMException e) {
//            gptService.updateMessageStatus(userMessage.getMessageId(), IntegerEnum.THREE.getValue());
//            return ResponseInfo.success();
//        }
//        chatMessage.setParentMessageId(userMessage.getMessageId());
//        gptService.saveChatMessage(chatMessage);
//        gptService.updateMessageStatus(userMessage.getMessageId(), IntegerEnum.TWO.getValue());
//        return ResponseInfo.success(DozerUtil.convertor(chatMessage, ChatMessageDTO.class));
//    }
//
//
//    public void sseChat(HttpServletResponse response, Boolean isWs, String uid, String conversationId) {
//        ChatMessageDTO chatMessage = gptService.getMessageByConverstationId(conversationId);
//        String prompt = chatMessage.getContent();
//        String version = chatMessage.getModelVersion();
//        ChatModelEnum modelEnum = ChatModelEnum.getEnum(chatMessage.getModel());
//        SseEmitter sseEmitter = createSse(uid);
//        if (sseEmitter == null) {
//            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
//            throw new BusinessException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
//        }
//        List<ChatMessageDTO> chatMessages = gptService.listMessageByConverstationId(uid, conversationId);
//        if (ValidatorUtil.isNullIncludeArray(chatMessages)) {
//            throw new BusinessException("消息发送失败");
//        }
//        Boolean error = false;
//        Integer status = ChatStatusEnum.SUCCESS.getValue();
//        try {
//            // ChatGPT、文心一言统一在SSEListener中处理流式返回，通义千问与讯飞星火\智谱清言单独处理
//            error = getLLM(modelEnum).streamChat(response, sseEmitter, chatMessages, isWs, isDraw(prompt), chatMessage.getChatId(), conversationId, prompt, version, uid);
//        } catch (BusinessException e) {
//            throw e;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ErrorException();
//        } finally {
//            gptService.updateMessageStatus(conversationId, error ? ChatStatusEnum.ERROR.getValue() : status);
//            if (error) {
//                throw new ErrorException("模型输出失败");
//            }
//        }
//    }
//
//    /**
//     * 判断是否需要画画
//     *
//     * @param prompt 输入内容
//     * @return
//     */
//    private Boolean isDraw(String prompt) {
//        // 检查是否有明确的画画词汇
//        for (String word : drawingWords) {
//            if (prompt.contains(word)) {
//                return true;
//            }
//        }
//        // 检查是否有指导性的画画语句
//        for (String instruction : drawingInstructions) {
//            if (prompt.contains(instruction)) {
//                return true;
//            }
//        }
//        // 检查是否有描述画画的动作的词或其他相关名词
//        // 这里只是一个示例，实际上需要更复杂的词性标注和语境分析
//        if (prompt.contains("画") || prompt.contains("绘画")) {
//            return true;
//        }
//        return false;
//    }
//
//}
