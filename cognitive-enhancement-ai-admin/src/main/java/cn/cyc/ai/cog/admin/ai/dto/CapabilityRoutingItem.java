package cn.cyc.ai.cog.admin.ai.dto;

import lombok.Data;

/**
 * 能力路由摘要（灰度指针 + 租户启停）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class CapabilityRoutingItem {

    /** 能力编码。 */
    private String capabilityCode;
    /** published版本号。 */
    private String publishedVersion;
    /** baseline版本号。 */
    private String baselineVersion;
    /** candidate版本号。 */
    private String candidateVersion;
    /** gray是否启用。 */
    private boolean grayEnabled;
    /** 租户是否启用。 */
    private boolean tenantEnabled;
}
