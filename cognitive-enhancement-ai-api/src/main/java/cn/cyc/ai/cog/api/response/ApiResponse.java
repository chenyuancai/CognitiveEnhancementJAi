package cn.cyc.ai.cog.api.response;

import cn.cyc.ai.cog.api.enums.ErrorCode;

import java.time.Instant;

/**
 * 平台统一响应对象。
 *
 * @param success 是否成功
 * @param code    统一响应码
 * @param message 响应说明
 * @param data    业务数据
 * @param traceId 链路标识
 * @param timestamp 响应时间戳（毫秒）
 * @param <T>     数据类型
 */
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        String traceId,
        long timestamp
) {

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

    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode, String traceId) {
        return failure(errorCode, errorCode.getMessage(), traceId);
    }

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
