package org.dows.gpt.tongyi;

import cn.hutool.core.util.StrUtil;
import org.dows.gpt.client.enums.ChatModelEnum;
import org.dows.gpt.common.exception.ValidateException;
import org.dows.gpt.llm.base.key.KeyUpdater;
import lombok.Data;

/**
 * 通义千问client
 * 文档地址：https://help.aliyun.com/zh/dashscope/developer-reference/model-square/?disableWebsiteRedirect=true
 *
 */
@Data
public class TongYiClient implements KeyUpdater {

    private String apiKey;

    public TongYiClient() {
    }

    public TongYiClient(Builder builder) {
        if (StrUtil.isBlank(builder.apiKey)) {
            throw new ValidateException("构造错误: apiKey不能为空");
        }
        this.apiKey = builder.apiKey;
    }


    @Override
    public String supportModel() {
        return ChatModelEnum.TONGYI.getValue();
    }

    @Override
    public void updateKey(KeyModel keyModel) {
        this.setApiKey(keyModel.getAppKey());
    }

    /**
     * 构造
     *
     * @return
     */
    public static TongYiClient.Builder builder() {
        return new TongYiClient.Builder();
    }

    public static final class Builder {
        private String apiKey;

        public Builder() {
        }

        public TongYiClient.Builder apiKey(String val) {
            apiKey = val;
            return this;
        }

        public TongYiClient build() {
            return new TongYiClient(this);
        }
    }

}
