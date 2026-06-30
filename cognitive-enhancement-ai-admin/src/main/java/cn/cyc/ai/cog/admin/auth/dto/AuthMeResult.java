package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * /api/admin/auth/me 响应体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeResult {

    /** 用户。 */
    private AuthMeUser user;
    /** 账户。 */
    private AuthMeAccount account;
    /** organization。 */
    private AuthMeOrganization organization;
    /** roles。 */
    private List<String> roles;
    /** permissions。 */
    private List<String> permissions;
    private Map<String, String> permissionAliases;
    /** menuTree。 */
    private List<AuthMeMenuNode> menuTree;
    /** 会员。 */
    private AuthMeMembership membership;
    /** 额度。 */
    private AuthMeQuota quota;
}
