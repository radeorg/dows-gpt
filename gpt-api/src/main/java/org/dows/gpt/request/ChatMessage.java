package org.dows.gpt.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private String id;
    private String sessionId;
    private String content;
    private String role; // user 或 assistant
    private Integer tokenCount;
    private LocalDateTime createTime;
}
