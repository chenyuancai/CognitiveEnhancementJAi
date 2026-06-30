package cn.cyc.ai.cog.app.tutoring.support;

import cn.cyc.ai.cog.common.context.TenantContext;

import java.util.function.Supplier;

/**
 * C 端租户上下文同步工具，将平台租户信息传播到 Runtime 租户上下文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class AppTutoringTenantSync {

    /**
     * 创建AppTutoringTenantSync。
     */
    private AppTutoringTenantSync() {
    }

    /**
     * 在 Runtime 租户上下文中执行有返回值动作，并在 finally 中清理。
     *
     * @param action 业务动作
     * @param <T>    返回类型
     * @return 动作结果
     */
    public static <T> T runWithRuntimeTenant(Supplier<T> action) {
        syncRuntimeTenantContext();
        try {
            return action.get();
        } finally {
            cn.cyc.ai.cog.runtime.security.TenantContext.clear();
        }
    }

    /**
     * 在 Runtime 租户上下文中执行无返回值动作。
     *
     * @param action 业务动作
     */
    public static void runWithRuntimeTenant(Runnable action) {
        runWithRuntimeTenant(() -> {
            action.run();
            return null;
        });
    }

    /**
     * 将平台租户编码与 ID 同步到 Runtime 租户上下文。
     */
    private static void syncRuntimeTenantContext() {
        cn.cyc.ai.cog.runtime.security.TenantContext.setTenantCode(TenantContext.currentTenantCode());
        cn.cyc.ai.cog.runtime.security.TenantContext.setTenantId(TenantContext.currentTenantId());
    }
}
