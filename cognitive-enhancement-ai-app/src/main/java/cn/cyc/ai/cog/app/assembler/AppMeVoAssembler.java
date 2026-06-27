package cn.cyc.ai.cog.app.assembler;

import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * C 端 {@link UserMeContext} → {@link AppMeResponse} 转换器。
 */
@Component
public class AppMeVoAssembler {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * 平台用户上下文转 C 端响应体。
     *
     * @param context 平台聚合快照
     * @return C 端 /me 响应
     */
    public AppMeResponse toResponse(UserMeContext context) {
        AppMeResponse response = new AppMeResponse();
        response.setUser(toUser(context.getUser()));
        response.setAccount(toAccount(context.getAccount()));
        response.setOrganization(toOrganization(context.getOrganization()));
        response.setSegment(context.getSegment());
        response.setMembership(toMembership(context.getMembership()));
        response.setQuota(toQuota(context.getQuota()));
        return response;
    }

    private AppMeResponse.AppMeUser toUser(UserMeContext.UserSnapshot user) {
        if (user == null) {
            return null;
        }
        AppMeResponse.AppMeUser dto = new AppMeResponse.AppMeUser();
        dto.setId(String.valueOf(user.getId()));
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        return dto;
    }

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

    private AppMeResponse.AppMeMembership toMembership(UserMeContext.MembershipSnapshot membership) {
        if (membership == null) {
            return null;
        }
        AppMeResponse.AppMeMembership dto = new AppMeResponse.AppMeMembership();
        dto.setLevelCode(membership.getLevelCode());
        dto.setLevelName(membership.getLevelName());
        if (membership.getExpireAt() != null) {
            dto.setExpireAt(membership.getExpireAt().atZone(java.time.ZoneId.systemDefault()).format(ISO));
        }
        return dto;
    }

    private AppMeResponse.AppMeQuota toQuota(UserMeContext.QuotaSnapshot quota) {
        if (quota == null) {
            return null;
        }
        AppMeResponse.AppMeQuota dto = new AppMeResponse.AppMeQuota();
        dto.setCycleRemaining(quota.getCycleRemaining());
        dto.setGiftRemaining(quota.getGiftRemaining());
        dto.setTopupRemaining(quota.getTopupRemaining());
        return dto;
    }
}
