package org.dows.gpt.wenxin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文本生成图片内容
 */
@Data
public class ImageData implements Serializable {

    /**
     * 固定值"image"
     */
    private String object;

    /**
     * 图片base64编码内容
     */
    @JsonProperty("b64_image")
    private String b64Image;

    /**
     * 序号
     */
    private Integer index;

}
