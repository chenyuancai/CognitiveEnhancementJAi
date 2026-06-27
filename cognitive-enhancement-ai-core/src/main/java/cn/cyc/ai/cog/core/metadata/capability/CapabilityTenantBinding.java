package cn.cyc.ai.cog.core.metadata.capability;

/**
 * 租户与 Capability 启停绑定。
 *
 * @param tenantCode     租户编码
 * @param capabilityCode 能力编码
 * @param enabled        是否启用
 * @author cyc
 */
public record CapabilityTenantBinding(
        String tenantCode,
        String capabilityCode,
        boolean enabled
) {
}
