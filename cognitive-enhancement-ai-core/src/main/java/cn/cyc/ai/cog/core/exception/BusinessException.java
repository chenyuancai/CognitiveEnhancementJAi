package cn.cyc.ai.cog.core.exception;

/**
 * Business异常
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Deprecated
public class BusinessException extends cn.cyc.ai.cog.common.exception.BusinessException {

    /**
     * 创建Business异常。
     *
     * @param message 消息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 创建Business异常。
     *
     * @param code 编码
     * @param message 消息
     */
    public BusinessException(String code, String message) {
        super(code, message);
    }

    /**
     * 创建Business异常。
     *
     * @param code 编码
     * @param message 消息
     * @param cause cause
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
