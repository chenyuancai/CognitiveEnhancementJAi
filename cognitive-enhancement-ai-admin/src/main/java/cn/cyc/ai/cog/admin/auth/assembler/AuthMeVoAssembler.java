package cn.cyc.ai.cog.admin.auth.assembler;

import cn.cyc.ai.cog.admin.auth.dto.AuthMeResponse;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * 平台 {@link UserMeContext} → Admin {@link AuthMeResponse} 基础字段转换器。
 * <p>
 * RBAC 角色、权限与菜单由 {@link cn.cyc.ai.cog.admin.auth.service.AuthMeService} 另行填充。
 * </p>
 */
@Component
public class AuthMeVoAssembler {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * 将平台用户上下文映射为 Admin /me 响应的基础字段。
     *
     * @param context 平台聚合快照
     * @return 不含 RBAC/菜单的响应体
     */
    public AuthMeResponse fromContext(UserMeContext context) {
        AuthMeResponse response = new AuthMeResponse();
        response.setUser(toUser(context.getUser()));
        response.setAccount(toAccount(context.getAccount()));
        response.setOrganization(toOrganization(context.getOrganization()));
        response.setSegment(context.getSegment());
        response.setMembership(toMembership(context.getMembership()));
        response.setQuota(toQuota(context.getQuota()));
        return response;
    }

    private AuthMeResponse.AuthMeUser toUser(UserMeContext.UserSnapshot user) {
        if (user == null) {
            return null;
        }
        AuthMeResponse.AuthMeUser dto = new AuthMeResponse.AuthMeUser();
        dto.setId(String.valueOf(user.getId()));
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        return dto;
    }

    private AuthMeResponse.AuthMeAccount toAccount(UserMeContext.AccountSnapshot account) {
        if (account == null) {
            return null;
        }
        AuthMeResponse.AuthMeAccount dto = new AuthMeResponse.AuthMeAccount();
        dto.setId(String.valueOf(account.getId()));
        dto.setAccountType(account.getAccountType());
        dto.setSegment(account.getSegment());
        dto.setDisplayName(account.getDisplayName());
        return dto;
    }

    private AuthMeResponse.AuthMeOrganization toOrganization(UserMeContext.OrganizationSnapshot organization) {
        if (organization == null) {
            return null;
        }
        AuthMeResponse.AuthMeOrganization dto = new AuthMeResponse.AuthMeOrganization();
        dto.setId(String.valueOf(organization.getId()));
        dto.setOrgName(organization.getOrgName());
        dto.setOrgType(organization.getOrgType());
        return dto;
    }

    private AuthMeResponse.AuthMeMembership toMembership(UserMeContext.MembershipSnapshot membership) {
        if (membership == null) {
            return null;
        }
        AuthMeResponse.AuthMeMembership dto = new AuthMeResponse.AuthMeMembership();
        dto.setLevelCode(membership.getLevelCode());
        dto.setLevelName(membership.getLevelName());
        if (membership.getExpireAt() != null) {
            dto.setExpireAt(membership.getExpireAt().atZone(java.time.ZoneId.systemDefault()).format(ISO));
        }
        return dto;
    }

    private AuthMeResponse.AuthMeQuota toQuota(UserMeContext.QuotaSnapshot quota) {
        if (quota == null) {
            return null;
        }
        AuthMeResponse.AuthMeQuota dto = new AuthMeResponse.AuthMeQuota();
        dto.setCycleRemaining(quota.getCycleRemaining());
        dto.setGiftRemaining(quota.getGiftRemaining());
        dto.setTopupRemaining(quota.getTopupRemaining());
        return dto;
    }
}
