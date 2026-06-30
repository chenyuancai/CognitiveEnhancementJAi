package cn.cyc.ai.cog.app.tutoring.access;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringConstants;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * C 端 AI 学习辅导访问门禁：校验会员权益与额度预检。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringAccessGate {

    /** 当前用户上下文服务。 */
    private final UserMeContextService userMeContextService;

    /** 会员等级仓储。 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** 会员权益支持组件。 */
    private final MembershipBenefitSupport membershipBenefitSupport;

    /** 额度服务。 */
    private final QuotaService quotaService;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造访问门禁组件。
     *
     * @param userMeContextService       当前用户上下文服务
     * @param membershipLevelRepository  会员等级仓储
     * @param membershipBenefitSupport   会员权益支持组件
     * @param quotaService               额度服务
     * @param properties                 学习辅导配置属性
     */
    public AppTutoringAccessGate(UserMeContextService userMeContextService,
                                 MembershipLevelRepository membershipLevelRepository,
                                 MembershipBenefitSupport membershipBenefitSupport,
                                 QuotaService quotaService,
                                 AppTutoringProperties properties) {
        this.userMeContextService = userMeContextService;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipBenefitSupport = membershipBenefitSupport;
        this.quotaService = quotaService;
        this.properties = properties;
    }

    /**
     * 校验当前用户是否允许使用 AI 学习辅导。
     *
     * @throws cn.cyc.ai.cog.common.exception.PlatformException 权益或额度不足时抛出
     */
    public void checkTutoringAllowed() {
        UserMeContext context = userMeContextService.buildForCurrentUser();
        MembershipLevel level = resolveMembershipLevel(context);
        if (!membershipBenefitSupport.hasBenefit(level.id(), level.benefitsJson(), AppTutoringConstants.BENEFIT_CODE)) {
            throw Errors.of(PlatformErrorCode.FORBIDDEN, "当前会员等级未开通 AI 带学");
        }
        if (context.getAccount() == null || context.getAccount().getId() == null) {
            throw Errors.of(PlatformErrorCode.ACCOUNT_NOT_FOUND);
        }
        QuotaAccount quota = quotaService.getByAccountId(context.getAccount().getId());
        long required = Math.max(1L, properties.getPreflightTokenAmount());
        if (totalRemaining(quota) < required) {
            throw Errors.of(PlatformErrorCode.RUNTIME_QUOTA_EXCEEDED);
        }
    }

    /**
     * 解析当前用户的会员等级。
     *
     * @param context 当前用户上下文
     * @return 会员等级
     */
    private MembershipLevel resolveMembershipLevel(UserMeContext context) {
        String levelCode = context.getMembership() == null ? "FREE" : context.getMembership().getLevelCode();
        if (!StringUtils.hasText(levelCode)) {
            levelCode = "FREE";
        }
        MembershipLevel level = membershipLevelRepository.findByCodeIfPresent(levelCode);
        return level == null
                ? membershipLevelRepository.requireDefaultForSegment(AppTutoringConstants.DEFAULT_SEGMENT)
                : level;
    }

    /**
     * 计算额度账户剩余总量。
     *
     * @param quota 额度账户
     * @return 剩余额度总量
     */
    private long totalRemaining(QuotaAccount quota) {
        return safe(quota.cycleRemaining()) + safe(quota.giftRemaining()) + safe(quota.topupRemaining());
    }

    /**
     * 将可空 Long 安全转换为 long。
     *
     * @param value 可空 Long 值
     * @return 非空 long 值
     */
    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
