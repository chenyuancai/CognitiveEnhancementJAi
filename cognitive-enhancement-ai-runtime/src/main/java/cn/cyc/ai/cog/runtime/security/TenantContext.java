package cn.cyc.ai.cog.runtime.security;

import org.springframework.util.StringUtils;

/**
 * 请求级租户上下文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class TenantContext {

    /** 默认租户编码。 */
    public static final String DEFAULT_TENANT_CODE = "default";

    /** CURRENT。 */
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
    /** 租户ID */
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    /**
     * 创建TenantContext。
     */
    private TenantContext() {
    }

    /**
     * 设置租户编码。
     *
     * @param tenantCode 租户编码
     */
    public static void setTenantCode(String tenantCode) {
        CURRENT.set(normalize(tenantCode));
        TENANT_ID.set(TenantIds.resolveId(tenantCode));
    }

    /**
     * 设置租户ID。
     *
     * @param tenantId 租户 ID
     */
    public static void setTenantId(Long tenantId) {
        if (tenantId != null) {
            TENANT_ID.set(tenantId);
        }
    }

    /**
     * 执行current租户编码。
     * @return 执行结果
     */
    public static String currentTenantCode() {
        return normalize(CURRENT.get());
    }

    /**
     * 执行current租户ID。
     * @return 执行结果
     */
    public static Long currentTenantId() {
        Long tenantId = TENANT_ID.get();
        return tenantId != null ? tenantId : TenantIds.PLATFORM_TENANT_ID;
    }

    /**
     * 执行clear。
     */
    public static void clear() {
        CURRENT.remove();
        TENANT_ID.remove();
    }

    /**
     * 执行normalize。
     *
     * @param tenantCode 租户编码
     * @return 执行结果
     */
    public static String normalize(String tenantCode) {
        return StringUtils.hasText(tenantCode) ? tenantCode : DEFAULT_TENANT_CODE;
    }
}
