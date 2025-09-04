package org.dows.gpt.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * websocket 转换对象
 *
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketData {

    /**
     * 心跳码
     */
    private String functionCode;

    /**
     * 消息
     */
    private Object message;


}
