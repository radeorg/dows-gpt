package org.dows.gpt.handler;

import org.springframework.web.socket.WebSocketHandler;

public interface ChatHandler extends WebSocketHandler {


    default void handle() {
    }
}
