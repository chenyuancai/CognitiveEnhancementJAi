package cn.cyc.ai.cog.runtime.security;

import org.springframework.util.StringUtils;

/**
 * 租户 ID 与编码互转（Center/Runtime 元数据隔离主键）。
 */
public final class TenantIds {

    public static final long PLATFORM_TENANT_ID = 1L;
    public static final String PLATFORM_TENANT_CODE = "platform";

    private TenantIds() {
    }

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

    public static String toCode(Long tenantId) {
        if (tenantId == null || tenantId == PLATFORM_TENANT_ID) {
            return PLATFORM_TENANT_CODE;
        }
        return String.valueOf(tenantId);
    }
}
