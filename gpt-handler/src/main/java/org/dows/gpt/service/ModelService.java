package org.dows.gpt.service;//package org.dows.gpt.service;
//
//import org.dows.gpt.client.model.command.ChatMessageCommand;
//import org.dows.gpt.client.model.dto.ChatMessageDTO;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.util.List;
//
///**
// * 大模型接口
// *
// */
//public interface ModelService {
//
//    /**
//     * 同步响应
//     *
//     * @param chatId
//     * @param version
//     * @param chatMessages
//     * @return
//     */
//    ChatMessageCommand chat(List<ChatMessageDTO> chatMessages, Boolean isDraw, Long chatId, String version);
//
//    /**
//     * 流式响应
//     *
//     * @return true 响应异常 false 响应正常
//     */
//    Boolean streamChat(HttpServletResponse response, SseEmitter sseEmitter, List<ChatMessageDTO> chatMessages, Boolean isWs, Boolean isDraw,
//                       Long chatId, String conversationId, String prompt, String version, String uid);
//
//}
