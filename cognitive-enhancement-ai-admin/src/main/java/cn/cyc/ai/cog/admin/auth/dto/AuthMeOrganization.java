package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

/**
 * AuthMeOrganization
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeOrganization {
    /** 主键 ID */
    private String id;
    /** org名称。 */
    private String orgName;
    /** org类型。 */
    private String orgType;
}
