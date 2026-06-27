package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.app.dto.AppLearningInvokeRequest;
import cn.cyc.ai.cog.app.dto.AppLearningModeItemVO;
import cn.cyc.ai.cog.app.dto.AppLearningModesVO;
import cn.cyc.ai.cog.app.support.AppReadCache;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * C 端学习链路服务：权益校验 + 委托 Runtime 能力执行。
 */
@Service
public class AppLearningService {

    private static final String BENEFIT_SCORING = "ai.scoring";
    private static final String BENEFIT_TUTORING = "ai.tutoring";
    private static final String BENEFIT_QA = "ai.qa_global";

    /** 用户上下文服务 */
    private final UserMeContextService userMeContextService;

    /** 会员等级仓储 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** 会员权益解析 */
    private final MembershipBenefitSupport membershipBenefitSupport;

    /** 运行时治理器（app-server 装配） */
    private final ObjectProvider<RuntimeHarness> runtimeHarnessProvider;

    /** 只读缓存 */
    private final AppReadCache appReadCache;

    /**
     * @param userMeContextService      用户上下文服务
     * @param membershipLevelRepository 会员等级仓储
     * @param membershipBenefitSupport  会员权益解析
     * @param runtimeHarnessProvider    运行时治理器
     * @param appReadCache              只读缓存
     */
    public AppLearningService(UserMeContextService userMeContextService,
                              MembershipLevelRepository membershipLevelRepository,
                              MembershipBenefitSupport membershipBenefitSupport,
                              ObjectProvider<RuntimeHarness> runtimeHarnessProvider,
                              AppReadCache appReadCache) {
        this.userMeContextService = userMeContextService;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipBenefitSupport = membershipBenefitSupport;
        this.runtimeHarnessProvider = runtimeHarnessProvider;
        this.appReadCache = appReadCache;
    }

    /**
     * 查询当前用户可用的学习模式。
     *
     * @return 学习模式列表
     */
    public AppLearningModesVO listModes() {
        MembershipLevel level = resolveMembershipLevel();
        AppLearningModesVO result = new AppLearningModesVO();
        result.getModes().add(modeItem("SCORING", "capability.qa.answer", BENEFIT_SCORING, level));
        result.getModes().add(modeItem("TUTORING", "capability.chat.generate", BENEFIT_TUTORING, level));
        result.getModes().add(modeItem("QA", "capability.qa.answer", BENEFIT_QA, level));
        return result;
    }

    /**
     * 按模式调用 AI 学习能力。
     *
     * @param request 调用请求
     * @return 能力执行结果
     */
    public CapabilityExecuteResponse invoke(AppLearningInvokeRequest request) {
        String mode = request.getMode().trim().toUpperCase(Locale.ROOT);
        AppLearningModeItemVO modeItem = listModes().getModes().stream()
                .filter(item -> mode.equals(item.getMode()))
                .findFirst()
                .orElseThrow(() -> Errors.of(PlatformErrorCode.BAD_REQUEST, "不支持的学习模式：" + request.getMode()));
        if (!modeItem.isEnabled()) {
            throw Errors.of(PlatformErrorCode.FORBIDDEN, modeItem.getReason());
        }
        RuntimeHarness runtimeHarness = runtimeHarnessProvider.getIfAvailable();
        if (runtimeHarness == null) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE);
        }
        return runtimeHarness.execute(new CapabilityExecuteRequest(
                modeItem.getCapabilityCode(),
                request.getInput(),
                request.getParameters()));
    }

    private AppLearningModeItemVO modeItem(String mode, String capabilityCode, String benefitCode,
                                           MembershipLevel level) {
        boolean enabled = membershipBenefitSupport.hasBenefit(level.id(), level.benefitsJson(), benefitCode);
        AppLearningModeItemVO item = new AppLearningModeItemVO();
        item.setMode(mode);
        item.setCapabilityCode(capabilityCode);
        item.setEnabled(enabled);
        if (!enabled) {
            item.setReason("当前会员等级未开通该能力，请升级会员");
        }
        return item;
    }

    private MembershipLevel resolveMembershipLevel() {
        UserMeContext context = userMeContextService.buildForCurrentUser();
        String levelCode = context.getMembership() == null ? "FREE" : context.getMembership().getLevelCode();
        if (!StringUtils.hasText(levelCode)) {
            levelCode = "FREE";
        }
        String cacheKey = "mbr-level:" + levelCode;
        String finalLevelCode = levelCode;
        return appReadCache.get(cacheKey, () -> {
            MembershipLevel level = membershipLevelRepository.findByCodeIfPresent(finalLevelCode);
            return level == null ? membershipLevelRepository.requireDefaultForSegment("2C") : level;
        });
    }
}
