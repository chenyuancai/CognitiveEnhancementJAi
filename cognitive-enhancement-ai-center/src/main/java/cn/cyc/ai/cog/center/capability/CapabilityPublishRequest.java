package cn.cyc.ai.cog.center.capability;

/**
 * 发布 Capability 版本请求。
 *
 * @param version 待发布版本号
 * @author cyc
 */
public record CapabilityPublishRequest(String capabilityCode, String version) {
}
