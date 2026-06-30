package cn.cyc.ai.cog.api.enums;

/**
 * 平台统一错误码。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /** 编码。 */
    private final String code;
    /** 消息。 */
    private final String message;
    /** http状态。 */
    private final int httpStatus;

    /**
     * 创建ErrorCode 枚举。
     *
     * @param code 编码
     * @param message 消息
     * @param httpStatus http状态
     */
    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * 获取编码。
     * @return 编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取消息。
     * @return 消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取Http状态。
     * @return Http状态
     */
    public int getHttpStatus() {
        return httpStatus;
    }
}
