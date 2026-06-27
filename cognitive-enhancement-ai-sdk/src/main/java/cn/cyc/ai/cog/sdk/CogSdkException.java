package cn.cyc.ai.cog.sdk;

/**
 * SDK 调用异常。
 *
 * @author cyc
 */
public class CogSdkException extends RuntimeException {

    private final int httpStatus;
    private final String code;
    private final String traceId;

    public CogSdkException(int httpStatus, String code, String message, String traceId) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.traceId = traceId;
    }

    public CogSdkException(int httpStatus, String code, String message, String traceId, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.traceId = traceId;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public String code() {
        return code;
    }

    public String traceId() {
        return traceId;
    }
}
