package cn.cyc.ai.cog.common.exception;

import cn.cyc.ai.cog.api.enums.ErrorCode;

/**
 * 业务异常：携带统一返回码，供全局异常处理器转换为 {@code ApiResponse}。
 *
 * @author cyc
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 对外 API 业务码（如 A0404）。 */
    private final String code;

    /** HTTP 级错误分类。 */
    private final ErrorCode category;

    public ServiceException(String message) {
        this(PlatformErrorCode.BUSINESS_ERROR, message);
    }

    /**
     * @deprecated 请使用 {@link #ServiceException(PlatformErrorCode)} 或 {@link Errors}
     */
    @Deprecated
    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
        this.category = resolveLegacyCategory(code);
    }

    public ServiceException(PlatformErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.category = errorCode.getCategory();
    }

    public ServiceException(PlatformErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.category = errorCode.getCategory();
    }

    public ServiceException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.category = resolveLegacyCategory(resultCode.getCode());
    }

    public ServiceException(IResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.category = resolveLegacyCategory(resultCode.getCode());
    }

    public String getCode() {
        return code;
    }

    public ErrorCode getCategory() {
        return category;
    }

    /** 断言为真，否则抛出业务异常。 */
    public static void assertTrue(boolean expression, PlatformErrorCode errorCode) {
        if (!expression) {
            throw new ServiceException(errorCode);
        }
    }

    /** 断言为真，否则抛出业务异常（自定义详情）。 */
    public static void assertTrue(boolean expression, PlatformErrorCode errorCode, String message) {
        if (!expression) {
            throw new ServiceException(errorCode, message);
        }
    }

    /** 断言对象非空，否则抛出业务异常。 */
    public static void assertNotNull(Object obj, PlatformErrorCode errorCode) {
        if (obj == null) {
            throw new ServiceException(errorCode);
        }
    }

    private static ErrorCode resolveLegacyCategory(String code) {
        if (code == null) {
            return ErrorCode.BUSINESS_ERROR;
        }
        for (ErrorCode value : ErrorCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return switch (code) {
            case "UNAUTHORIZED" -> ErrorCode.UNAUTHORIZED;
            case "FORBIDDEN" -> ErrorCode.FORBIDDEN;
            case "NOT_FOUND" -> ErrorCode.NOT_FOUND;
            case "CONFLICT", "QUOTA_INSUFFICIENT" -> ErrorCode.CONFLICT;
            case "BAD_REQUEST", "INVALID_ARGUMENT" -> ErrorCode.BAD_REQUEST;
            case "TOO_MANY_REQUESTS" -> ErrorCode.TOO_MANY_REQUESTS;
            case "SERVICE_UNAVAILABLE" -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> ErrorCode.BUSINESS_ERROR;
        };
    }
}
