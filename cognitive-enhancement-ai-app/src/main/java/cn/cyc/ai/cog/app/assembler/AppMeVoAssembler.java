package cn.cyc.ai.cog.app.assembler;

import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.app.support.AppBillingLabelSupport;
import cn.cyc.ai.cog.app.support.QuotaLabelFormatter;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * C 端 {@link UserMeContext} → {@link AppMeResponse} 转换器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppMeVoAssembler {

    /** ISO。 */
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * 平台用户上下文转 C 端响应体。
     *
     * @param context 平台聚合快照
     * @return C 端 /me 响应
     */
    public AppMeResponse toResponse(UserMeContext context) {
        AppMeResponse response = new AppMeResponse();
        if (context.getAccount() != null) {
            response.setAccountId(String.valueOf(context.getAccount().getId()));
        }
        if (context.getUser() != null) {
            response.setUserId(context.getUser().getId());
        }
        response.setUser(toUser(context.getUser()));
        response.setAccount(toAccount(context.getAccount()));
        response.setOrganization(toOrganization(context.getOrganization()));
        response.setSegment(context.getSegment());
        response.setMembership(toMembership(context.getMembership()));
        response.setQuota(toQuota(context.getQuota()));
        return response;
    }

    /**
     * 转换为用户。
     *
     * @param user 用户
     * @return 转换结果
     */
    private AppMeResponse.AppMeUser toUser(UserMeContext.UserSnapshot user) {
        if (user == null) {
            return null;
        }
        AppMeResponse.AppMeUser dto = new AppMeResponse.AppMeUser();
        dto.setId(String.valueOf(user.getId()));
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setDisplayName(resolveDisplayName(user));
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        return dto;
    }

    /**
     * 转换为账户。
     *
     * @param account 账户
     * @return 转换结果
     */
    private AppMeResponse.AppMeAccount toAccount(UserMeContext.AccountSnapshot account) {
        if (account == null) {
            return null;
        }
        AppMeResponse.AppMeAccount dto = new AppMeResponse.AppMeAccount();
        dto.setId(String.valueOf(account.getId()));
        dto.setAccountType(account.getAccountType());
        dto.setSegment(account.getSegment());
        dto.setDisplayName(account.getDisplayName());
        return dto;
    }

    /**
     * 转换为Organization。
     *
     * @param organization organization
     * @return 转换结果
     */
    private AppMeResponse.AppMeOrganization toOrganization(UserMeContext.OrganizationSnapshot organization) {
        if (organization == null) {
            return null;
        }
        AppMeResponse.AppMeOrganization dto = new AppMeResponse.AppMeOrganization();
        dto.setId(String.valueOf(organization.getId()));
        dto.setOrgName(organization.getOrgName());
        dto.setOrgType(organization.getOrgType());
        return dto;
    }

    /**
     * 转换为会员。
     *
     * @param membership 会员
     * @return 转换结果
     */
    private AppMeResponse.AppMeMembership toMembership(UserMeContext.MembershipSnapshot membership) {
        if (membership == null) {
            return null;
        }
        AppMeResponse.AppMeMembership dto = new AppMeResponse.AppMeMembership();
        dto.setLevelCode(membership.getLevelCode());
        dto.setLevelName(membership.getLevelName());
        if (membership.getExpireAt() != null) {
            String formatted = membership.getExpireAt().atZone(java.time.ZoneId.systemDefault()).format(ISO);
            dto.setExpireAt(formatted);
            dto.setExpiresAt(formatted);
            dto.setRenewAtLabel(formatRenewLabel(membership.getExpireAt()));
        }
        return dto;
    }

    /**
     * 会员到期友好续费文案。
     */
    private String formatRenewLabel(java.time.LocalDateTime expireAt) {
        return expireAt.toLocalDate().toString().replace('-', '/') + " 续费";
    }

    private String resolveDisplayName(UserMeContext.UserSnapshot user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }

    /**
     * 转换为额度。
     *
     * @param quota 额度
     * @return 转换结果
     */
    private AppMeResponse.AppMeQuota toQuota(UserMeContext.QuotaSnapshot quota) {
        if (quota == null) {
            return null;
        }
        AppMeResponse.AppMeQuota dto = new AppMeResponse.AppMeQuota();
        long cycle = safe(quota.getCycleRemaining());
        long gift = safe(quota.getGiftRemaining());
        long topup = safe(quota.getTopupRemaining());
        long remaining = cycle + gift + topup;
        long total = remaining;
        dto.setTotal(total);
        dto.setUsed(0L);
        dto.setRemaining(remaining);
        dto.setRemainingLabel(QuotaLabelFormatter.format(remaining));
        dto.setWarningThreshold(AppBillingLabelSupport.quotaWarningThreshold(total));
        dto.setCycleTotal(cycle);
        dto.setCycleUsed(0L);
        dto.setCycleRemaining(cycle);
        dto.setGiftRemaining(gift);
        dto.setTopupRemaining(topup);
        return dto;
    }

    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
