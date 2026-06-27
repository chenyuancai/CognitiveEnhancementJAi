package cn.cyc.ai.cog.platform.iam.support;

/**
 * 认证相关安全配置键常量。
 */
public final class AuthConfigKeys {

    private AuthConfigKeys() {
    }

    /** 是否开放用户名密码注册，默认 true。 */
    public static final String REGISTER_USERNAME = "auth.register.username";

    /** 是否开放手机号注册，默认 false。 */
    public static final String REGISTER_PHONE = "auth.register.phone";

    /** 是否开放邮箱注册，默认 false。 */
    public static final String REGISTER_EMAIL = "auth.register.email";
}
