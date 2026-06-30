package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

/**
 * AuthMeMembership
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeMembership {
    /** 等级编码。 */
    private String levelCode;
    /** 等级名称。 */
    private String levelName;
    /** expireAt。 */
    private String expireAt;
}
