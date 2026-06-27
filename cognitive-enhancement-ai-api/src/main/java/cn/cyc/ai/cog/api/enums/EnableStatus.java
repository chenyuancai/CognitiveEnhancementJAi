package cn.cyc.ai.cog.api.enums;

/**
 * 通用启用/禁用状态（租户、角色、套餐、字典等）。
 */
public enum EnableStatus implements CodedEnum {

    ENABLED,
    DISABLED;

    public static EnableStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("状态码不能为空");
        }
        return EnableStatus.valueOf(code);
    }

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
