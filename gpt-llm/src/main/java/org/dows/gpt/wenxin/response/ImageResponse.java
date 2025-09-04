package org.dows.gpt.wenxin.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 文本生成图片返回
 *
 */
@Data
@ToString
public class ImageResponse implements Serializable {

    private String id;

    private String object;

    private Long created;

    /**
     * 生成图片结果
     */
    private List<ImageData> data;

    /**
     * 使用量
     */
    private Usage usage;

}
