package org.dows.gpt.request;

import lombok.Data;

@Data
public class ChatRequest {

    // 数据协议http,text,json,xml
    private String protocol;
    private String data;   // 用户输入的消息

    private String modelType; // 选择的大模型，例如 "chatgpt" 或 "local"

    //private ModelType modelType;
    private String prompt;
    private String userId;
    private String sessionId;
}
