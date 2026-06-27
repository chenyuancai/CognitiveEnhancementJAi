package cn.cyc.ai.cog.center.capability;

/**
 * 配置 Capability 租户启停请求。
 *
 * @param enabled 是否启用
 * @author cyc
 */
public record CapabilityTenantBindingRequest(String capabilityCode, String tenantCode, boolean enabled) {
}
