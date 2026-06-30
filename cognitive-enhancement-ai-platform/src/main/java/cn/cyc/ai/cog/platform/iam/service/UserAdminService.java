package cn.cyc.ai.cog.platform.iam.service;

import cn.cyc.ai.cog.api.enums.IamUserStatus;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.dto.UserAdminResult;
import cn.cyc.ai.cog.platform.iam.dto.UserPageQuery;
import cn.cyc.ai.cog.platform.iam.dto.UserStatusUpdateRequest;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.common.spi.UserSessionRevoker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * 管理端用户查询与状态治理服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class UserAdminService {

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /** 会话吊销（封禁时可选） */
    private final ObjectProvider<UserSessionRevoker> userSessionRevoker;

    /**
     * @param iamUserRepository             IAM 用户仓储
     * @param accountMembershipRepository   账户会员关系仓储
     * @param userSessionRevoker              会话吊销 SPI
     */
    public UserAdminService(IamUserRepository iamUserRepository,
                            AccountMembershipRepository accountMembershipRepository,
                            ObjectProvider<UserSessionRevoker> userSessionRevoker) {
        this.iamUserRepository = iamUserRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.userSessionRevoker = userSessionRevoker;
    }

    /**
     * 分页查询用户（含会员等级摘要）。
     *
     * @param query 分页与筛选条件
     * @return 用户管理视图分页结果
     */
    public PageResult<UserAdminResult> page(UserPageQuery query) {
        return iamUserRepository.page(query).map(this::toResult);
    }

    /**
     * 查询用户详情。
     *
     * @param id 用户 ID
     * @return 用户管理视图
     */
    public UserAdminResult detail(Long id) {
        return toResult(iamUserRepository.requireById(id));
    }

    /**
     * 更新用户状态（禁止修改当前登录用户）。
     *
     * @param id      用户 ID
     * @param request 状态更新请求
     * @return 更新后的用户管理视图
     */
    public UserAdminResult updateStatus(Long id, UserStatusUpdateRequest request) {
        if (id.equals(UserContext.currentUserId())) {
            throw Errors.of(PlatformErrorCode.USER_CANNOT_MODIFY_SELF);
        }
        if (!IamUserStatus.isValid(request.getStatus())) {
            throw Errors.of(PlatformErrorCode.USER_STATUS_INVALID);
        }
        IamUser existing = iamUserRepository.requireById(id);
        IamUserStatus targetStatus = IamUserStatus.fromCode(request.getStatus());
        IamUser updated = iamUserRepository.updateStatus(
                id, targetStatus.code(), request.getBanReason(), request.getBanUntil());
        if (targetStatus.isBanned()) {
            userSessionRevoker.ifAvailable(revoker -> revoker.revokeByPrincipalName(existing.username()));
        }
        return toResult(updated);
    }

    /**
     * 转换为结果。
     *
     * @param user 用户
     * @return 转换结果
     */
    private UserAdminResult toResult(IamUser user) {
        UserAdminResult result = new UserAdminResult();
        result.setId(String.valueOf(user.id()));
        result.setUsername(user.username());
        result.setNickname(user.nickname());
        result.setEmail(user.email());
        result.setPhone(user.phone());
        result.setAvatarUrl(user.avatarUrl());
        result.setStatus(user.status());
        result.setUserType(user.userType());
        result.setTenantId(user.tenantId() == null ? null : String.valueOf(user.tenantId()));
        result.setAccountId(user.primaryAccountId() == null ? null : String.valueOf(user.primaryAccountId()));
        if (user.primaryAccountId() != null) {
            AccountMembership membership = accountMembershipRepository.findByAccountId(user.primaryAccountId());
            if (membership != null) {
                result.setLevelCode(membership.levelCode());
            }
        }
        return result;
    }
}
