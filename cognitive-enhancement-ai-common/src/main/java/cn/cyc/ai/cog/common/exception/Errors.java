package cn.cyc.ai.cog.common.exception;

/**
 * 统一业务异常抛出入口，避免散落 {@code new ServiceException("NOT_FOUND", "...")} 魔法字符串。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class Errors {

    /**
     * 创建Errors。
     */
    private Errors() {
    }

    /**
     * 执行of。
     *
     * @param errorCode 错误编码
     * @return 执行结果
     */
    public static ServiceException of(PlatformErrorCode errorCode) {
        return new ServiceException(errorCode);
    }

    /**
     * 执行of。
     *
     * @param errorCode 错误编码
     * @param detailMessage detail消息
     * @return 执行结果
     */
    public static ServiceException of(PlatformErrorCode errorCode, String detailMessage) {
        return new ServiceException(errorCode, detailMessage);
    }

    /**
     * 执行throw错误。
     *
     * @param errorCode 错误编码
     */
    public static void throwError(PlatformErrorCode errorCode) {
        throw of(errorCode);
    }

    /**
     * 执行throw错误。
     *
     * @param errorCode 错误编码
     * @param detailMessage detail消息
     */
    public static void throwError(PlatformErrorCode errorCode, String detailMessage) {
        throw of(errorCode, detailMessage);
    }

    /**
     * 执行throwIf。
     *
     * @param condition condition
     * @param errorCode 错误编码
     */
    public static void throwIf(boolean condition, PlatformErrorCode errorCode) {
        if (condition) {
            throwError(errorCode);
        }
    }

    /**
     * 执行throwIf。
     *
     * @param condition condition
     * @param errorCode 错误编码
     * @param detailMessage detail消息
     */
    public static void throwIf(boolean condition, PlatformErrorCode errorCode, String detailMessage) {
        if (condition) {
            throwError(errorCode, detailMessage);
        }
    }
}
