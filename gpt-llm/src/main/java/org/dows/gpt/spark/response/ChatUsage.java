package org.dows.gpt.spark.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 使用量
 *
 */
@Data
public class ChatUsage implements Serializable {
    private static final long serialVersionUID = 2181817132625461079L;

    /**
     * 使用量
     */
    private Usage text;

}
