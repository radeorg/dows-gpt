package org.dows.gpt.key;

import lombok.Data;

// 1. 首先创建一个统一的密钥更新器接口
public interface KeyUpdater {

    @Data
    class KeyModel {
        private Long id;
        private String appId;
        private String appKey;
        private String appSecret;
        private String model;
    }

    // 返回支持的模型类型
    String supportModel();

    // 更新密钥方法
    void updateKey(KeyModel keyModel);
}