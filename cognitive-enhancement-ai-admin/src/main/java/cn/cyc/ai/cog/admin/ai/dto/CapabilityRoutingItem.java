package cn.cyc.ai.cog.admin.ai.dto;

import lombok.Data;

/**
 * 能力路由摘要（灰度指针 + 租户启停）。
 */
@Data
public class CapabilityRoutingItem {

    private String capabilityCode;
    private String publishedVersion;
    private String baselineVersion;
    private String candidateVersion;
    private boolean grayEnabled;
    private boolean tenantEnabled;
}
