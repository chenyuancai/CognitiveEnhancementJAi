package cn.cyc.ai.cog.common.exception;

/**
 * 运行时 / Center 业务异常，统一走 {@link ServiceException} 与 {@link PlatformErrorCode}。
 * <p>
 * 兼容旧写法 {@code new BusinessException("NOT_FOUND", "详情")}，HTTP 响应使用 A04xx 标准码。
 */
public class BusinessException extends ServiceException {

    private static final long serialVersionUID = 1L;

    /** 旧语义码（如 NOT_FOUND），仅用于测试与日志；API 请使用 {@link #getCode()}。 */
    private final String semanticCode;

    public BusinessException(String message) {
        super(PlatformErrorCode.BUSINESS_ERROR, message);
        this.semanticCode = null;
    }

    /**
     * @param semanticCode 旧语义码（NOT_FOUND / FORBIDDEN 等）
     * @param message      中文详情
     */
    public BusinessException(String semanticCode, String message) {
        super(resolveErrorCode(semanticCode), message);
        this.semanticCode = semanticCode;
    }

    public BusinessException(String semanticCode, String message, Throwable cause) {
        super(resolveErrorCode(semanticCode), message);
        this.semanticCode = semanticCode;
        if (cause != null) {
            initCause(cause);
        }
    }

    public BusinessException(PlatformErrorCode errorCode) {
        super(errorCode);
        this.semanticCode = null;
    }

    public BusinessException(PlatformErrorCode errorCode, String message) {
        super(errorCode, message);
        this.semanticCode = null;
    }

    public String getSemanticCode() {
        return semanticCode;
    }

    private static PlatformErrorCode resolveErrorCode(String semanticCode) {
        if (semanticCode == null || semanticCode.isBlank()) {
            return PlatformErrorCode.BUSINESS_ERROR;
        }
        return switch (semanticCode) {
            case "NOT_FOUND" -> PlatformErrorCode.NOT_FOUND;
            case "FORBIDDEN" -> PlatformErrorCode.FORBIDDEN;
            case "UNAUTHORIZED" -> PlatformErrorCode.UNAUTHORIZED;
            case "CONFLICT", "CONFIRMATION_REQUIRED", "CAPABILITY_DISABLED", "MODEL_UNAVAILABLE" ->
                    PlatformErrorCode.CONFLICT;
            case "INVALID_ARGUMENT" -> PlatformErrorCode.BAD_REQUEST;
            case "TOO_MANY_REQUESTS" -> PlatformErrorCode.TOO_MANY_REQUESTS;
            case "SERVICE_UNAVAILABLE" -> PlatformErrorCode.SERVICE_UNAVAILABLE;
            default -> PlatformErrorCode.BUSINESS_ERROR;
        };
    }
}
