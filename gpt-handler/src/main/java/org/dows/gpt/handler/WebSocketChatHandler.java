package org.dows.gpt.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.gpt.client.LLMClientFactory;
import org.dows.gpt.session.SessionUser;
import org.dows.gpt.token.JwtTokenProvider;
import org.dows.gpt.utils.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChatHandler extends TextWebSocketHandler implements ChatHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final LLMClientFactory LLMClientFactory;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        HttpHeaders headers = session.getHandshakeHeaders();

        // 1、优先从请求头获取 Token
        String token = extractTokenFromHeaders(headers);
        if (token == null) {
            // 2、如果请求头没有 Token，则从 Cookie 获取
            token = extractTokenFromCookies(headers.get("cookie"));
        }

        if (token == null) {
            // 2、websocket支持从请求参数中传递票据
            token = extractTokenFromRequest(session);
        }

        // 3、校验 Token
        if (!validToken(token)) {
            log.warn("WebSocket 连接失败，Token 无效：{}", session.getId());
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        log.info("WebSocket 连接成功，用户身份验证通过：{}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // json: modelType , message
        String userMessage = message.getPayload();
        log.info("用户发送消息：{}", userMessage);
        try {
            Map map = JsonUtils.toMap(userMessage);
            String modelType = (String) map.get("modelType");
            String prompt = (String) map.get("message");
            // 调用 AI 大模型接口，并流式返回消息
            LLMClientFactory.getClient(modelType).streamChat(prompt, session);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("AI 服务异常，请稍后重试。"));
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 连接关闭：{}", session.getId());
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractTokenFromHeaders(HttpHeaders headers) {
        List<String> authHeaders = headers.get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractTokenFromRequest(WebSocketSession session) {
        if (session.getUri() != null) {
            return UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst("WebSocket-Authorization");
        }
        return null;
    }

    /**
     * 从 Cookie 中提取 Token
     */
    private String extractTokenFromCookies(List<String> cookieHeaders) {
        if (cookieHeaders == null || cookieHeaders.isEmpty()) {
            return null;
        }
        for (String cookieHeader : cookieHeaders) {
            String[] cookies = cookieHeader.split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("AUTH-TOKEN=")) {
                    return cookie.substring("AUTH-TOKEN=".length());
                }
            }
        }
        return null;
    }

    private boolean validToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        SessionUser sessionUser = jwtTokenProvider.validateUserToken(token);
        return sessionUser != null;
    }
}