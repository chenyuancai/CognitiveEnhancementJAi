package cn.cyc.ai.cog.core.metadata.capability;

/**
 * 租户与 Capability 启停绑定。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityTenantBinding(
        String tenantCode,
        String capabilityCode,
        boolean enabled
) {
}
