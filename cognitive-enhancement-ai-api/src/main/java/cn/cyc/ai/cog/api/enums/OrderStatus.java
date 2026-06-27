package cn.cyc.ai.cog.api.enums;

/**
 * 订单状态。
 */
public enum OrderStatus implements CodedEnum {

    PENDING,
    PAID,
    FULFILLED,
    REFUNDED,
    CANCELLED,
    CLOSED;

    public static OrderStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("订单状态码不能为空");
        }
        return OrderStatus.valueOf(code);
    }

    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        for (OrderStatus status : values()) {
            if (status.matches(code)) {
                return true;
            }
        }
        return false;
    }
}
