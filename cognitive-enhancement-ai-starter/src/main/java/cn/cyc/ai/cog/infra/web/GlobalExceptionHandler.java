package cn.cyc.ai.cog.infra.web;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.exception.BusinessException;
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

/**
 * 统一异常转标准响应。
 *
 * @author cyc
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常日志。
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常。
     *
     * @param exception 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = mapBusinessError(exception);
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
     * 将业务异常编码映射为标准 API 错误码。
     *
     * @param exception 业务异常
     * @return 标准错误码
     */
    private ErrorCode mapBusinessError(BusinessException exception) {
        if ("INVALID_ARGUMENT".equals(exception.getCode())) {
            return ErrorCode.BAD_REQUEST;
        }
        if ("NOT_FOUND".equals(exception.getCode())) {
            return ErrorCode.NOT_FOUND;
        }
        if ("CONFLICT".equals(exception.getCode())) {
            return ErrorCode.CONFLICT;
        }
        return ErrorCode.BUSINESS_ERROR;
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
