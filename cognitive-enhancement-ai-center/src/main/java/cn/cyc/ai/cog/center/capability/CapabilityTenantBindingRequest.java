package cn.cyc.ai.cog.center.capability;

/**
 * 配置 Capability 租户启停请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityTenantBindingRequest(String capabilityCode, String tenantCode, boolean enabled) {
}
