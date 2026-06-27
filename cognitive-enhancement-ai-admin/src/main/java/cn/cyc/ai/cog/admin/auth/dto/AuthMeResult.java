package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * /api/admin/auth/me 响应体。
 */
@Data
public class AuthMeResult {

    private AuthMeUser user;
    private AuthMeAccount account;
    private AuthMeOrganization organization;
    private List<String> roles;
    private List<String> permissions;
    private Map<String, String> permissionAliases;
    private List<AuthMeMenuNode> menuTree;
    private AuthMeMembership membership;
    private AuthMeQuota quota;
}
