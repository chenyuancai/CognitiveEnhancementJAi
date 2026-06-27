package cn.cyc.ai.cog.common.context;

import cn.cyc.ai.cog.common.constant.CommonConstants;

/**
 * 当前请求租户上下文持有者（ThreadLocal）。
 *
 * @author cyc
 */
public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();

    private TenantContext() {
    }

    /** 设置当前租户 ID。 */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /** 设置当前租户编码（JWT/网关透传）。 */
    public static void setTenantCode(String tenantCode) {
        TENANT_CODE.set(tenantCode);
    }

    /** @deprecated 使用 {@link #setTenantCode(String)} */
    @Deprecated
    public static void set(String tenantCode) {
        setTenantCode(tenantCode);
    }

    /** 获取当前租户 ID，缺省返回平台租户。 */
    public static Long currentTenantId() {
        Long tenantId = TENANT_ID.get();
        return tenantId == null ? CommonConstants.DEFAULT_TENANT_ID : tenantId;
    }

    /** 获取当前租户编码，缺省返回 platform。 */
    public static String currentTenantCode() {
        String tenantCode = TENANT_CODE.get();
        return tenantCode == null ? CommonConstants.DEFAULT_TENANT : tenantCode;
    }

    /** @deprecated 使用 {@link #currentTenantCode()} */
    @Deprecated
    public static String get() {
        return currentTenantCode();
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_CODE.remove();
    }
}
