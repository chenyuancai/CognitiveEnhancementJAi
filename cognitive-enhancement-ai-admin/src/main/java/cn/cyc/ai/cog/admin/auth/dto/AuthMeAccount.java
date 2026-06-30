package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

/**
 * AuthMeAccount
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeAccount {
    /** 主键 ID */
    private String id;
    /** 账户类型。 */
    private String accountType;
    /** segment。 */
    private String segment;
    /** display名称。 */
    private String displayName;
}
