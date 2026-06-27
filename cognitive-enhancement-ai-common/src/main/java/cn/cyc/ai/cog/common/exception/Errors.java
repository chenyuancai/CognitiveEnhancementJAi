package cn.cyc.ai.cog.common.exception;

/**
 * 统一业务异常抛出入口，避免散落 {@code new ServiceException("NOT_FOUND", "...")} 魔法字符串。
 */
public final class Errors {

    private Errors() {
    }

    public static ServiceException of(PlatformErrorCode errorCode) {
        return new ServiceException(errorCode);
    }

    public static ServiceException of(PlatformErrorCode errorCode, String detailMessage) {
        return new ServiceException(errorCode, detailMessage);
    }

    public static void throwError(PlatformErrorCode errorCode) {
        throw of(errorCode);
    }

    public static void throwError(PlatformErrorCode errorCode, String detailMessage) {
        throw of(errorCode, detailMessage);
    }

    public static void throwIf(boolean condition, PlatformErrorCode errorCode) {
        if (condition) {
            throwError(errorCode);
        }
    }

    public static void throwIf(boolean condition, PlatformErrorCode errorCode, String detailMessage) {
        if (condition) {
            throwError(errorCode, detailMessage);
        }
    }
}
