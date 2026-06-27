package cn.cyc.ai.cog.infra.web;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.core.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * 统一异常转标准响应。
 *
 * @author cyc
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常日志。
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常（含 {@link cn.cyc.ai.cog.common.exception.BusinessException}）。
     *
     * @param exception 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(ServiceException exception) {
        ErrorCode errorCode = exception.getCategory() != null
                ? exception.getCategory()
                : mapServiceError(exception.getCode());
        log.warn("捕获业务异常, traceId={}, code={}, message={}",
                TraceContext.getTraceId(), exception.getCode(), exception.getMessage());
        return buildResponse(errorCode, exception.getMessage());
    }

    /**
     * 处理非法参数异常。
     *
     * @param exception 非法参数异常
     * @return 统一错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("捕获非法参数异常, traceId={}, message={}",
                TraceContext.getTraceId(), exception.getMessage());
        return buildResponse(ErrorCode.BAD_REQUEST, exception.getMessage());
    }

    /**
     * 处理常见 Web 请求参数异常。
     *
     * @param exception 请求异常
     * @return 统一错误响应
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequestExceptions(Exception exception) {
        log.warn("捕获请求格式异常, traceId={}, type={}, message={}",
                TraceContext.getTraceId(), exception.getClass().getSimpleName(), exception.getMessage());
        return buildResponse(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage());
    }

    /**
     * 兜底处理未知异常。
     *
     * @param exception 未知异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("未处理异常，traceId={}", TraceContext.getTraceId(), exception);
        return buildResponse(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 将业务异常码映射为标准 API 错误码。
     *
     * @param code 业务码
     * @return 标准错误码
     */
    private ErrorCode mapServiceError(String code) {
        if (code == null) {
            return ErrorCode.BUSINESS_ERROR;
        }
        return switch (code) {
            case "A0401", "UNAUTHORIZED" -> ErrorCode.UNAUTHORIZED;
            case "A0403", "FORBIDDEN" -> ErrorCode.FORBIDDEN;
            case "A0404", "NOT_FOUND" -> ErrorCode.NOT_FOUND;
            case "A0409", "CONFLICT" -> ErrorCode.CONFLICT;
            case "A0400", "BAD_REQUEST" -> ErrorCode.BAD_REQUEST;
            case "A0429", "TOO_MANY_REQUESTS" -> ErrorCode.TOO_MANY_REQUESTS;
            default -> ErrorCode.BUSINESS_ERROR;
        };
    }

    /**
     * 构造统一响应实体。
     *
     * @param errorCode 标准错误码
     * @param message   返回消息
     * @return 响应实体
     */
    private ResponseEntity<ApiResponse<Void>> buildResponse(ErrorCode errorCode, String message) {
        ApiResponse<Void> body = ApiResponse.failure(errorCode, message, TraceContext.getTraceId());
        return ResponseEntity.status(HttpStatusCode.valueOf(errorCode.getHttpStatus())).body(body);
    }
}
