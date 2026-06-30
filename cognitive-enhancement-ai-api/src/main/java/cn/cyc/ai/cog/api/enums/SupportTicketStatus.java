package cn.cyc.ai.cog.api.enums;

/**
 * 客服工单状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum SupportTicketStatus implements CodedEnum {

    /** open。 */
    OPEN,
    /** inprogress。 */
    IN_PROGRESS,
    /** resolved。 */
    RESOLVED,
    CLOSED;

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static SupportTicketStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("工单状态码不能为空");
        }
        return SupportTicketStatus.valueOf(code);
    }

    /**
     * 判断是否为Valid。
     *
     * @param code 编码
     * @return 是否满足条件
     */
    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        for (SupportTicketStatus status : values()) {
            if (status.matches(code)) {
                return true;
            }
        }
        return false;
    }
}
