package cn.cyc.ai.cog.common.context;

import java.util.Optional;

/**
 * 当前请求用户上下文持有者（ThreadLocal）。
 *
 * <p>由认证拦截器在请求开始时 set，结束时务必 clear，避免线程池串号。</p>
 *
 * @author cyc
 */
public final class UserContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    public static AuthUser get() {
        return HOLDER.get();
    }

    public static Optional<AuthUser> current() {
        return Optional.ofNullable(HOLDER.get());
    }

    public static Long currentUserId() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    public static String currentUsername() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
