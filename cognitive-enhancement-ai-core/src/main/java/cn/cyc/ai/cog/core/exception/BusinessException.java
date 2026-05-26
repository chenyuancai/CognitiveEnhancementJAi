package cn.cyc.ai.cog.core.exception;

/**
 * 平台通用业务异常。
 */
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        this(null, message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
