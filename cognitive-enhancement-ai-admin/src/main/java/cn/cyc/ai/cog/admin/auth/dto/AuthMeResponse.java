package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * /api/admin/auth/me 响应体。
 */
@Data
public class AuthMeResponse {

    private AuthMeUser user;
    private AuthMeAccount account;
    private AuthMeOrganization organization;
    private String segment;
    private List<String> roles;
    private List<String> permissions;
    private Map<String, String> permissionAliases;
    private List<AuthMeMenuNode> menuTree;
    private AuthMeMembership membership;
    private AuthMeQuota quota;

    @Data
    public static class AuthMeUser {
        private String id;
        private String username;
        private String nickname;
        private String avatarUrl;
        private String status;
    }

    @Data
    public static class AuthMeAccount {
        private String id;
        private String accountType;
        private String segment;
        private String displayName;
    }

    @Data
    public static class AuthMeOrganization {
        private String id;
        private String orgName;
        private String orgType;
    }

    @Data
    public static class AuthMeMenuNode {
        private String key;
        private String title;
        private String path;
        private String icon;
        private List<AuthMeMenuNode> children;
    }

    @Data
    public static class AuthMeMembership {
        private String levelCode;
        private String levelName;
        private String expireAt;
    }

    @Data
    public static class AuthMeQuota {
        private Long cycleRemaining;
        private Long giftRemaining;
        private Long topupRemaining;
    }
}
