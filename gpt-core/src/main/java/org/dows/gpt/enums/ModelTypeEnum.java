package org.dows.gpt.enums;

public enum ModelTypeEnum {

    DEEPSEEK_R1("deepseek-r1", "DeepSeek-R1","deepseek"),
    DEEPSEEK_V3("deepseek-v3", "DeepSeek-V3","deepseek"),
    CHATGPT_4O("gpt-4o", "ChatGPT-4o","gpt"),
    LOCAL("local", "本地大模型",""),
    QWEN("qwen", "通义千问","qwen"),
    ;

    private final String code;

    private final String name;

    private final String channel;

    ModelTypeEnum(String code, String name,String channel) {
        this.code = code;
        this.name = name;
        this.channel = channel;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getChannel() {
        return channel;
    }
}