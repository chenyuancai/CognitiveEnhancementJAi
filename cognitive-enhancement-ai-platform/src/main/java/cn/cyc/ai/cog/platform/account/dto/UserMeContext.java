package cn.cyc.ai.cog.platform.account.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前用户商业上下文快照（C 端 / Admin 共用聚合结果，不含 RBAC 菜单）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class UserMeContext {

    /** 登录用户摘要 */
    private UserSnapshot user;

    /** 商业账户摘要 */
    private AccountSnapshot account;

    /** 所属组织（B 端可选） */
    private OrganizationSnapshot organization;

    /** 客群分段 */
    private String segment;

    /** 会员摘要 */
    private MembershipSnapshot membership;

    /** 额度摘要 */
    private QuotaSnapshot quota;

    /**
     * 用户摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class UserSnapshot {

        /** 用户 ID */
        private Long id;

        /** 登录名 */
        private String username;

        /** 昵称 */
        private String nickname;

        /** 邮箱 */
        private String email;

        /** 头像 URL */
        private String avatarUrl;

        /** 账户状态 */
        private String status;
    }

    /**
     * 商业账户摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class AccountSnapshot {

        /** 账户 ID */
        private Long id;

        /** 账户类型 */
        private String accountType;

        /** 客群分段 */
        private String segment;

        /** 展示名称 */
        private String displayName;
    }

    /**
     * 组织摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class OrganizationSnapshot {

        /** 组织 ID */
        private Long id;

        /** 组织名称 */
        private String orgName;

        /** 组织类型 */
        private String orgType;
    }

    /**
     * 会员摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class MembershipSnapshot {

        /** 等级编码 */
        private String levelCode;

        /** 等级名称 */
        private String levelName;

        /** 到期时间 */
        private LocalDateTime expireAt;
    }

    /**
     * 额度摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class QuotaSnapshot {

        /** 周期剩余 Token */
        private Long cycleRemaining;

        /** 赠送剩余 Token */
        private Long giftRemaining;

        /** 充值剩余 Token */
        private Long topupRemaining;
    }
}
