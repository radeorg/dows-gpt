package org.dows.gpt.request;

import lombok.Data;

@Data
public class ChatRequest {

    // 数据协议oss,http,string
    private String protocol;
    // 用户输入的内容
    private String content;
    // [deepseek-v3,deepseek-r1]
    private String modelType;
    // 系统提示词
    private String prompt;

    private String appId;

    private String userId;
    private String sessionId;

    /**
     * 组织根ID
     */
    private Long orgRootId;
    /**
     * 组织树ID
     */
    private Long orgTreeId;

    /**
     * JD名
     */
    private String jdName;
    /**
     * 简历文件名
     */
    private String fileName;

    /**
     * 操作者ID
     */
    private Long operatorId;
}
