package cn.cyc.ai.cog.platform.account.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.account.repository.AccountRepository;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;

/**
 * 解析当前请求绑定的商业账户 ID。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AccountContextResolver {

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 商业账户仓储 */
    private final AccountRepository accountRepository;

    /**
     * @param iamUserRepository IAM 用户仓储
     * @param accountRepository 商业账户仓储
     */
    public AccountContextResolver(IamUserRepository iamUserRepository, AccountRepository accountRepository) {
        this.iamUserRepository = iamUserRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * 解析当前登录用户绑定的商业账户 ID。
     *
     * @return 账户 ID
     */
    public Long resolveCurrentAccountId() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw Errors.of(PlatformErrorCode.NOT_LOGGED_IN);
        }
        return resolveAccountIdByUserId(userId);
    }

    /**
     * 按用户 ID 解析商业账户 ID。
     *
     * @param userId 用户 ID
     * @return 账户 ID
     */
    public Long resolveAccountIdByUserId(Long userId) {
        IamUser user = iamUserRepository.requireById(userId);
        if (user.primaryAccountId() != null) {
            return user.primaryAccountId();
        }
        Account account = accountRepository.findByOwnerUserId(userId);
        if (account == null) {
            throw Errors.of(PlatformErrorCode.USER_ACCOUNT_NOT_BOUND);
        }
        return account.id();
    }
}
