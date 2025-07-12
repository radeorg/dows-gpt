package org.dows.gpt.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSession {
    private String id;
    private String userId;
    private String modelType;
    // private ModelType modelType;
    private LocalDateTime createTime;
}