package cn.cyc.ai.cog.sdk;

/**
 * SDK 调用异常。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class CogSdkException extends RuntimeException {

    /** http状态。 */
    private final int httpStatus;
    /** 编码。 */
    private final String code;
    /** 链路 Trace ID */
    private final String traceId;

    /**
     * 创建CogSdk异常。
     *
     * @param httpStatus http状态
     * @param code 编码
     * @param message 消息
     * @param traceId 链路 Trace ID
     */
    public CogSdkException(int httpStatus, String code, String message, String traceId) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.traceId = traceId;
    }

    /**
     * 创建CogSdk异常。
     *
     * @param httpStatus http状态
     * @param code 编码
     * @param message 消息
     * @param traceId 链路 Trace ID
     * @param cause cause
     */
    public CogSdkException(int httpStatus, String code, String message, String traceId, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.traceId = traceId;
    }

    /**
     * 执行http状态。
     * @return 执行结果
     */
    public int httpStatus() {
        return httpStatus;
    }

    /**
     * 执行编码。
     * @return 执行结果
     */
    public String code() {
        return code;
    }

    /**
     * 执行链路 Trace ID。
     * @return 执行结果
     */
    public String traceId() {
        return traceId;
    }
}
