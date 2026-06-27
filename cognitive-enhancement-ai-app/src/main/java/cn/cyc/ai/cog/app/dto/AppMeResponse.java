package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端 {@code /api/app/auth/me} 响应体。
 */
@Data
public class AppMeResponse {

    /** 当前登录用户 */
    private AppMeUser user;

    /** 商业账户 */
    private AppMeAccount account;

    /** 所属组织（B 端可选） */
    private AppMeOrganization organization;

    /** 客群分段 */
    private String segment;

    /** 会员信息 */
    private AppMeMembership membership;

    /** 额度摘要 */
    private AppMeQuota quota;

    /**
     * 用户摘要。
     */
    @Data
    public static class AppMeUser {

        /** 用户 ID */
        private String id;

        /** 登录名 */
        private String username;

        /** 昵称 */
        private String nickname;

        /** 头像 URL */
        private String avatarUrl;

        /** 账户状态 */
        private String status;
    }

    /**
     * 商业账户摘要。
     */
    @Data
    public static class AppMeAccount {

        /** 账户 ID */
        private String id;

        /** 账户类型 */
        private String accountType;

        /** 客群分段 */
        private String segment;

        /** 展示名称 */
        private String displayName;
    }

    /**
     * 组织摘要。
     */
    @Data
    public static class AppMeOrganization {

        /** 组织 ID */
        private String id;

        /** 组织名称 */
        private String orgName;

        /** 组织类型 */
        private String orgType;
    }

    /**
     * 会员摘要。
     */
    @Data
    public static class AppMeMembership {

        /** 等级编码 */
        private String levelCode;

        /** 等级名称 */
        private String levelName;

        /** 到期时间（ISO-8601） */
        private String expireAt;
    }

    /**
     * 额度摘要。
     */
    @Data
    public static class AppMeQuota {

        /** 周期剩余 Token */
        private Long cycleRemaining;

        /** 赠送剩余 Token */
        private Long giftRemaining;

        /** 充值剩余 Token */
        private Long topupRemaining;
    }
}
