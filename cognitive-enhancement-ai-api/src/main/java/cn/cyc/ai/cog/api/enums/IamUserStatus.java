package cn.cyc.ai.cog.api.enums;

/**
 * IAM 用户状态（含封禁）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum IamUserStatus implements CodedEnum {

    /** 是否启用。 */
    ENABLED,
    /** disabled。 */
    DISABLED,
    BANNED;

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static IamUserStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("用户状态码不能为空");
        }
        return IamUserStatus.valueOf(code);
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
        for (IamUserStatus status : values()) {
            if (status.matches(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为Banned。
     * @return 是否满足条件
     */
    public boolean isBanned() {
        return this == BANNED;
    }
}
