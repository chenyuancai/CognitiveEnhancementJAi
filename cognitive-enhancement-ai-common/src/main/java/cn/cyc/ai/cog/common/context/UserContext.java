package cn.cyc.ai.cog.common.context;

import java.util.Optional;

/**
 * 当前请求用户上下文持有者（ThreadLocal）。
 * <p>由认证拦截器在请求开始时 set，结束时务必 clear，避免线程池串号。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class UserContext {

    /** HOLDER。 */
    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    /**
     * 创建UserContext。
     */
    private UserContext() {
    }

    /**
     * 执行set。
     *
     * @param user 用户
     */
    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    /**
     * 执行get。
     * @return 执行结果
     */
    public static AuthUser get() {
        return HOLDER.get();
    }

    /**
     * 执行current。
     * @return 执行结果
     */
    public static Optional<AuthUser> current() {
        return Optional.ofNullable(HOLDER.get());
    }

    /**
     * 执行current用户ID。
     * @return 执行结果
     */
    public static Long currentUserId() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    /**
     * 执行currentUsername。
     * @return 执行结果
     */
    public static String currentUsername() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    /**
     * 执行clear。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
