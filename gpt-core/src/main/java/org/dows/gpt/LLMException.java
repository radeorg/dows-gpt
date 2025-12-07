package org.dows.gpt;

import lombok.Getter;

/**
 * 大模型 异常处理
 *
 */
@Getter
public class LLMException extends RuntimeException {

    private static final long serialVersionUID = 3053312855506511893L;

    private Integer code;

    private String message;

    public LLMException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public LLMException(String message) {
        super(message);
        //this.code = ResponseEnum.ERROR.getCode();
        this.message = message;
    }

    public LLMException(Integer code, String message, Throwable t) {
        super(message, t);
        this.code = code;
        this.message = message;
    }

}
