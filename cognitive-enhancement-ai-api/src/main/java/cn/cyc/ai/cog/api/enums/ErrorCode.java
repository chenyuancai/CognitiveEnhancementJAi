package cn.cyc.ai.cog.api.enums;

/**
 * 平台统一错误码。
 */
public enum ErrorCode {

    SUCCESS("0", "成功", 200),
    BAD_REQUEST("A0400", "请求参数错误", 400),
    UNAUTHORIZED("A0401", "未认证或认证已失效", 401),
    FORBIDDEN("A0403", "无权访问当前资源", 403),
    NOT_FOUND("A0404", "资源不存在", 404),
    CONFLICT("A0409", "资源状态冲突", 409),
    TOO_MANY_REQUESTS("A0429", "请求过于频繁", 429),
    BUSINESS_ERROR("B0500", "业务处理失败", 400),
    SYSTEM_ERROR("C0500", "系统开小差了，请稍后再试", 500),
    SERVICE_UNAVAILABLE("C0503", "服务暂不可用", 503);

    private final String code;
    private final String message;
    private final int httpStatus;

    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
