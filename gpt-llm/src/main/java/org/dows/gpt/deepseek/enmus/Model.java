package org.dows.gpt.deepseek.enmus;

import lombok.Getter;

/**
 * 模型枚举
 */
@Getter
public enum Model {

    /**
     * deepseek-reasoner 代表R1模型
     */
    CHAT("deepseek-chat"),

    REASONER("deepseek-reasoner"),

    ;
    private final String name;

    Model(String name) {
        this.name = name;
    }

}
