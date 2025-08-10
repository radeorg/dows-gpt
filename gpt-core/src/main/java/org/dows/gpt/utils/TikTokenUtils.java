package org.dows.gpt.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

/**
 * token计算工具类
 *
 */
@Slf4j
public class TikTokenUtils {
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    /**
     * 通过Encoding和text获取编码数组
     *
     * @param enc  Encoding类型
     * @param text 文本信息
     * @return 编码数组
     */
    private static IntArrayList encode(Encoding enc, String text) {
        return StringUtils.isBlank(text) ? new IntArrayList() : enc.encode(text);
    }

    /**
     * 通过Encoding和encoded数组反推text信息
     *
     * @param enc     Encoding
     * @param encoded 编码数组
     * @return 编码数组对应的文本信息
     */
    private static String decode(Encoding enc, IntArrayList encoded) {
        return enc.decode(encoded);
    }

    /**
     * 获取一个Encoding对象，通过Encoding类型
     *
     * @param encodingType encodingType
     * @return Encoding
     */
    private static Encoding getEncoding(EncodingType encodingType) {
        return registry.getEncoding(encodingType);
    }

    /**
     * 获取encode的编码数组
     *
     * @param text 文本信息
     * @return 编码数组
     */
    private static IntArrayList encode(EncodingType encodingType, String text) {
        if (StringUtils.isBlank(text)) {
            return new IntArrayList();
        }
        Encoding enc = getEncoding(encodingType);
        return enc.encode(text);
    }

    /**
     * 通过EncodingType和encoded编码数组，反推字符串文本
     *
     * @param encodingType encodingType
     * @param encoded      编码数组
     * @return 编码数组对应的字符串
     */
    private static String decode(EncodingType encodingType, IntArrayList encoded) {
        Encoding enc = getEncoding(encodingType);
        return enc.decode(encoded);
    }

    /**
     * 获取一个Encoding对象，通过模型名称
     *
     * @param modelName 模型名称
     * @return Encoding
     */
    private static Encoding getEncoding(String modelName) {
        Optional<ModelType> optional = ModelType.fromName(modelName);
        ModelType modelType = null;
        if (optional.isPresent()) {
            modelType = optional.get();
        } else {
            if (modelName.startsWith("gpt-4") || modelName.startsWith("claude-1.3")) {
                modelType = ModelType.GPT_4;
            }
            if (modelName.startsWith("gpt-3.5") || modelName.startsWith("claude-instant-1.1")) {
                modelType = ModelType.GPT_3_5_TURBO;
            }
            if (modelName.startsWith("qwen")) {
                modelType = ModelType.GPT_3_5_TURBO;
            }
            if (modelName.startsWith("deepseek")) {
                modelType = ModelType.GPT_3_5_TURBO;
            }
            if (modelName.startsWith("Baichuan")) {
                modelType = ModelType.GPT_3_5_TURBO;
            }
            if (modelName.startsWith("ERNIE")) {
                modelType = ModelType.GPT_3_5_TURBO;
            }
            if (modelName.startsWith("spark")) {
                modelType = ModelType.GPT_3_5_TURBO;
            }
        }
        return registry.getEncodingForModel(modelType);
    }

    /**
     * 获取encode的编码数组，通过模型名称
     *
     * @param text 文本信息
     * @return 编码数组
     */
    private static IntArrayList encode(String modelName, String text) {
        if (StringUtils.isBlank(text)) {
            return new IntArrayList();
        }
        Encoding enc = getEncoding(modelName);
        if (Objects.isNull(enc)) {
            log.warn("[{}]模型不存在或者暂不支持计算tokens，直接返回tokens==0", modelName);
            return new IntArrayList();
        }
        return enc.encode(text);
    }

    /**
     * 通过模型名称和encoded编码数组，反推字符串文本
     *
     * @param modelName 模型名
     * @param encoded   编码数组
     * @return 返回源文本
     */
    private static String decode(String modelName, IntArrayList encoded) {
        Encoding enc = getEncoding(modelName);
        return enc.decode(encoded);
    }

    /**
     * 获取modelType
     *
     * @param name 模型名称
     * @return ModelType
     */
    private static ModelType getModelTypeByName(String name) {
        if (name.startsWith("gpt-3.5") || name.startsWith("claude-instant-1.1")) {
            return ModelType.GPT_3_5_TURBO;
        }
        if (name.startsWith("gpt-4") || name.startsWith("claude-1.3")) {
            return ModelType.GPT_4;
        }
        for (ModelType modelType : ModelType.values()) {
            if (modelType.getName().equals(name)) {
                return modelType;
            }
        }
        log.warn("[{}]模型不存在或者暂不支持计算tokens", name);
        return null;
    }

    /**
     * 通过Encoding计算text信息的tokens
     *
     * @param enc  Encoding类型
     * @param text 文本信息
     * @return tokens数量
     */
    public static int tokens(Encoding enc, String text) {
        return encode(enc, text).size();
    }

    /**
     * 计算指定字符串的tokens，通过EncodingType
     *
     * @param encodingType encodingType
     * @param text         文本信息
     * @return tokens数量
     */
    public static int tokens(EncodingType encodingType, String text) {
        return encode(encodingType, text).size();
    }

    /**
     * 通过模型名称, 计算指定字符串的tokens
     *
     * @param modelName 模型名称
     * @param text      文本信息
     * @return tokens数量
     */
    public static Long tokens(String modelName, String text) {
        return (long) encode(modelName, text).size();
    }
}