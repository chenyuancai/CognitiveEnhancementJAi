package cn.cyc.ai.cog.core.exception;

/**
 * @deprecated 使用 {@link cn.cyc.ai.cog.common.exception.BusinessException}
 */
@Deprecated
public class BusinessException extends cn.cyc.ai.cog.common.exception.BusinessException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
