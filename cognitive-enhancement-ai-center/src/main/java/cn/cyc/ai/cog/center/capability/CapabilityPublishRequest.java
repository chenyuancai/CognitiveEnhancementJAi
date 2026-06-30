package cn.cyc.ai.cog.center.capability;

/**
 * 发布 Capability 版本请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityPublishRequest(String capabilityCode, String version) {
}
