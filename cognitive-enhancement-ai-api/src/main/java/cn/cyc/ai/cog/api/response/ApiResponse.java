package cn.cyc.ai.cog.api.response;

import cn.cyc.ai.cog.api.enums.ErrorCode;

import java.time.Instant;

/**
 * 平台统一响应对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        String traceId,
        long timestamp
) {

    /**
     * 构建成功响应。
     *
     * @param data 数据
     * @param traceId 链路 Trace ID
     * @return 统一响应对象
     */
    public static <T> ApiResponse<T> success(T data, String traceId) {
        return new ApiResponse<>(
                true,
                ErrorCode.SUCCESS.getCode(),
                ErrorCode.SUCCESS.getMessage(),
                data,
                traceId,
                Instant.now().toEpochMilli()
        );
    }

    /**
     * 构建成功响应。
     *
     * @param data 数据
     * @return 统一响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    /**
     * 构建失败响应。
     *
     * @param errorCode 错误编码
     * @param traceId 链路 Trace ID
     * @return 统一响应对象
     */
    public static ApiResponse<Void> failure(ErrorCode errorCode, String traceId) {
        return failure(errorCode, errorCode.getMessage(), traceId);
    }

    /**
     * 构建失败响应。
     *
     * @param errorCode 错误编码
     * @param message 消息
     * @param traceId 链路 Trace ID
     * @return 统一响应对象
     */
    public static ApiResponse<Void> failure(ErrorCode errorCode, String message, String traceId) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                message,
                null,
                traceId,
                Instant.now().toEpochMilli()
        );
    }
}
