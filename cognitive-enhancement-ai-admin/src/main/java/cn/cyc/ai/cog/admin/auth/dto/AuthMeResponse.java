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
public class AuthMeResponse {

    /** 用户。 */
    private AuthMeUser user;
    /** 账户。 */
    private AuthMeAccount account;
    /** organization。 */
    private AuthMeOrganization organization;
    /** segment。 */
    private String segment;
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

    /**
     * AuthMeUser
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AuthMeUser {
        /** 主键 ID */
        private String id;
        /** username。 */
        private String username;
        /** nickname。 */
        private String nickname;
        /** avatar地址。 */
        private String avatarUrl;
        /** 状态。 */
        private String status;
    }

    /**
     * AuthMeAccount
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AuthMeAccount {
        /** 主键 ID */
        private String id;
        /** 账户类型。 */
        private String accountType;
        /** segment。 */
        private String segment;
        /** display名称。 */
        private String displayName;
    }

    /**
     * AuthMeOrganization
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AuthMeOrganization {
        /** 主键 ID */
        private String id;
        /** org名称。 */
        private String orgName;
        /** org类型。 */
        private String orgType;
    }

    /**
     * AuthMeMenuNode
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AuthMeMenuNode {
        /** 键。 */
        private String key;
        /** 标题。 */
        private String title;
        /** 路径。 */
        private String path;
        /** icon。 */
        private String icon;
        /** children。 */
        private List<AuthMeMenuNode> children;
    }

    /**
     * AuthMeMembership
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AuthMeMembership {
        /** 等级编码。 */
        private String levelCode;
        /** 等级名称。 */
        private String levelName;
        /** expireAt。 */
        private String expireAt;
    }

    /**
     * AuthMeQuota
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AuthMeQuota {
        /** cycleRemaining。 */
        private Long cycleRemaining;
        /** giftRemaining。 */
        private Long giftRemaining;
        /** topupRemaining。 */
        private Long topupRemaining;
    }
}
