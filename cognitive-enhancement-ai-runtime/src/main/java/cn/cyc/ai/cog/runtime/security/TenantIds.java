package cn.cyc.ai.cog.runtime.security;

import org.springframework.util.StringUtils;

/**
 * 租户 ID 与编码互转（Center/Runtime 元数据隔离主键）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class TenantIds {

    /** 平台租户ID */
    public static final long PLATFORM_TENANT_ID = 1L;
    /** 平台租户编码。 */
    public static final String PLATFORM_TENANT_CODE = "platform";

    /**
     * 创建TenantIds。
     */
    private TenantIds() {
    }

    /**
     * 执行resolveID。
     *
     * @param tenantCode 租户编码
     * @return 执行结果
     */
    public static long resolveId(String tenantCode) {
        if (!StringUtils.hasText(tenantCode)) {
            return PLATFORM_TENANT_ID;
        }
        String normalized = TenantContext.normalize(tenantCode);
        if ("default".equals(normalized) || PLATFORM_TENANT_CODE.equals(normalized)) {
            return PLATFORM_TENANT_ID;
        }
        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException ignored) {
            return PLATFORM_TENANT_ID;
        }
    }

    /**
     * 转换为编码。
     *
     * @param tenantId 租户 ID
     * @return 转换结果
     */
    public static String toCode(Long tenantId) {
        if (tenantId == null || tenantId == PLATFORM_TENANT_ID) {
            return PLATFORM_TENANT_CODE;
        }
        return String.valueOf(tenantId);
    }
}
