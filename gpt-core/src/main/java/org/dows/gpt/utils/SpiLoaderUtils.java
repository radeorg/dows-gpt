package org.dows.gpt.utils;

import java.util.ServiceLoader;

/**
 * @description SPI服务加载工具类
 */
public class SpiLoaderUtils {

    public static <T> T load(Class<T> serviceClass) {
        ServiceLoader<T> loader = ServiceLoader.load(serviceClass);
        for (T service : loader) {
            return service; // 返回找到的第一个实现
        }
        throw new IllegalStateException("No implementation found for " + serviceClass.getName());
    }

}
