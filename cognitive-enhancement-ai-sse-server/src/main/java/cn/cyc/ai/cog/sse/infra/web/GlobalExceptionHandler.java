package cn.cyc.ai.cog.sse.infra.web;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.exception.ServiceException;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SSE-Server 统一异常处理。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常。
     *
     * @param exception 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceException(ServiceException exception) {
        ErrorCode errorCode = exception.getCategory() != null ? exception.getCategory() : ErrorCode.BUSINESS_ERROR;
        log.warn("业务异常 code={}, message={}", exception.getCode(), exception.getMessage());
        return buildResponse(errorCode, exception.getMessage());
    }

    /**
     * 处理参数校验异常。
     *
     * @param exception 校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse(ErrorCode.BAD_REQUEST.getMessage());
        return buildResponse(ErrorCode.BAD_REQUEST, message);
    }

    /**
     * 处理请求体不可读异常。
     *
     * @param exception 解析异常
     * @return 统一错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException exception) {
        return buildResponse(ErrorCode.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 处理未捕获异常。
     *
     * @param exception 异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception exception) {
        log.error("未处理异常", exception);
        return buildResponse(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 构建响应。
     *
     * @param errorCode 错误编码
     * @param message 消息
     * @return 构建结果
     */
    private ResponseEntity<ApiResponse<Void>> buildResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(HttpStatusCode.valueOf(errorCode.getHttpStatus()))
                .body(ApiResponse.failure(errorCode, message, null));
    }
}
