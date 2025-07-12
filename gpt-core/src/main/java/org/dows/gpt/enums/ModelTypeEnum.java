package org.dows.gpt.enums;

public enum ModelTypeEnum {

    DEEPSEEK_R1("deepseek-r1", "DeepSeek-R1"),
    DEEPSEEK_V3("deepseek-v3", "DeepSeek-V3"),
    CHATGPT_4O("gpt-4o", "ChatGPT-4o"),
    LOCAL("local", "本地大模型"),
    ;

    private final String code;

    private final String name;

    ModelTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}