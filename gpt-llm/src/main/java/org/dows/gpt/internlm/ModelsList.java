package org.dows.gpt.internlm;

import lombok.Data;

import java.util.List;

/**
 * 模型列表
 *
 */
@Data
public class ModelsList {

    /**
     * 模型数据
     */
    private List<Model> data;

}
