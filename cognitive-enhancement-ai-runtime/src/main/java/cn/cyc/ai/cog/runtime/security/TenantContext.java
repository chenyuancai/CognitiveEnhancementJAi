package cn.cyc.ai.cog.runtime.security;

import org.springframework.util.StringUtils;

/**
 * 请求级租户上下文。
 *
 * @author cyc
 */
public final class TenantContext {

    public static final String DEFAULT_TENANT_CODE = "default";

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantCode(String tenantCode) {
        CURRENT.set(normalize(tenantCode));
        TENANT_ID.set(TenantIds.resolveId(tenantCode));
    }

    public static void setTenantId(Long tenantId) {
        if (tenantId != null) {
            TENANT_ID.set(tenantId);
        }
    }

    public static String currentTenantCode() {
        return normalize(CURRENT.get());
    }

    public static Long currentTenantId() {
        Long tenantId = TENANT_ID.get();
        return tenantId != null ? tenantId : TenantIds.PLATFORM_TENANT_ID;
    }

    public static void clear() {
        CURRENT.remove();
        TENANT_ID.remove();
    }

    public static String normalize(String tenantCode) {
        return StringUtils.hasText(tenantCode) ? tenantCode : DEFAULT_TENANT_CODE;
    }
}
