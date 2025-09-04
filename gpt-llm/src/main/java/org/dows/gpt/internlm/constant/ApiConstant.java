package org.dows.gpt.internlm.constant;

/**
 * 月之暗面 接口常量
 *
 */
public interface ApiConstant {

    String BASE_COMPLETION_URL = "https://internlm-chat.intern-ai.org.cn/puyu/api/v1";

    /**
     * 对话
     */
    String CHAT_COMPLETION_URL = BASE_COMPLETION_URL + "/chat/completions";

    /**
     * 获取模型列表
     */
    String CHAT_LIST_MODELS_URL = BASE_COMPLETION_URL + "/models";

}
