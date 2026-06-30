package cn.cyc.ai.cog.api.enums;

/**
 * 通用启用/禁用状态（租户、角色、套餐、字典等）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum EnableStatus implements CodedEnum {

    /** 是否启用。 */
    ENABLED,
    DISABLED;

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static EnableStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("状态码不能为空");
        }
        return EnableStatus.valueOf(code);
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
        for (EnableStatus status : values()) {
            if (status.matches(code)) {
                return true;
            }
        }
        return false;
    }
}
