package cn.cyc.ai.cog.platform.iam.entity;

/**
 * 用户类型常量。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class UserType {

    /**
     * 创建UserType。
     */
    private UserType() {
    }

    /** 后台运营人员。 */
    public static final String ADMIN = "ADMIN";

    /** C 端用户。 */
    public static final String CUSTOMER = "CUSTOMER";
}
