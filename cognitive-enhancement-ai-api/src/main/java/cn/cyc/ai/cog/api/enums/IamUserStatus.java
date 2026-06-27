package cn.cyc.ai.cog.api.enums;

/**
 * IAM 用户状态（含封禁）。
 */
public enum IamUserStatus implements CodedEnum {

    ENABLED,
    DISABLED,
    BANNED;

    public static IamUserStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("用户状态码不能为空");
        }
        return IamUserStatus.valueOf(code);
    }

    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        for (IamUserStatus status : values()) {
            if (status.matches(code)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBanned() {
        return this == BANNED;
    }
}
