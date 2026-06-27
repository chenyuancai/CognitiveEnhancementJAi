package cn.cyc.ai.cog.api.enums;

/**
 * 客服工单状态。
 */
public enum SupportTicketStatus implements CodedEnum {

    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    public static SupportTicketStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("工单状态码不能为空");
        }
        return SupportTicketStatus.valueOf(code);
    }

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
