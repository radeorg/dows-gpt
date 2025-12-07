package org.dows.gpt.sse;

import lombok.Data;

import java.time.Duration;

@Data
public class JsonSseMessage implements SseMessage {

    private String id;
    private String event;
    private Duration retry;
    private String comment;
    private Object data;
}
