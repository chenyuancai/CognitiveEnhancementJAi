package cn.cyc.ai.cog.bridge.usage;

import cn.cyc.ai.cog.platform.account.service.AccountContextResolver;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.service.RuntimeUsageProperties;
import cn.cyc.ai.cog.runtime.usage.spi.RuntimeUsageAccountService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * 将 Runtime 用量扣减桥接到 Admin {@link QuotaService}（Token 池）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
@Primary
@ConditionalOnProperty(name = "cog.runtime.usage.account.backend", havingValue = "admin-quota")
public class AdminQuotaRuntimeUsageAccountService implements RuntimeUsageAccountService {

    /** 额度服务。 */
    private final QuotaService quotaService;
    /** 账户上下文Resolver。 */
    private final AccountContextResolver accountContextResolver;
    /** properties。 */
    private final RuntimeUsageProperties properties;

    /**
     * 创建管理后台额度运行时Usage账户服务。
     */
    public AdminQuotaRuntimeUsageAccountService(QuotaService quotaService,
                                                AccountContextResolver accountContextResolver,
                                                RuntimeUsageProperties properties) {
        this.quotaService = quotaService;
        this.accountContextResolver = accountContextResolver;
        this.properties = properties;
    }

    /**
     * 执行checkBeforeExecution。
     *
     * @param capabilityCode 能力编码
     */
    @Override
    public void checkBeforeExecution(String capabilityCode) {
        if (!properties.getAccount().isEnabled()) {
            return;
        }
        long tokens = toTokens(properties.getAccount().getPreflightCostAmount());
        if (tokens <= 0) {
            tokens = 1L;
        }
        QuotaAccount quota = quotaService.getByAccountId(resolveAccountId());
        if (totalRemaining(quota) < tokens) {
            throw new BusinessException("TOO_MANY_REQUESTS", "账户 Token 额度不足");
        }
    }

    /**
     * 执行recordUsage。
     *
     * @param usageRecord usageRecord
     * @return 执行结果
     */
    @Override
    public UsageAccount recordUsage(UsageRecord usageRecord) {
        if (!properties.getAccount().isEnabled()) {
            return currentAccount();
        }
        long tokens = resolveTokenAmount(usageRecord);
        if (tokens <= 0) {
            return currentAccount();
        }
        try {
            QuotaAccount quota = quotaService.deduct(
                    resolveAccountId(),
                    tokens,
                    "runtime:" + usageRecord.traceId(),
                    UserContext.currentUserId(),
                    "CAPABILITY",
                    usageRecord.capabilityCode());
            return toUsageAccount(quota);
        } catch (ServiceException ex) {
            if ("QUOTA_INSUFFICIENT".equals(ex.getCode())) {
                throw new BusinessException("TOO_MANY_REQUESTS", ex.getMessage());
            }
            throw ex;
        }
    }

    /**
     * 执行current账户。
     * @return 执行结果
     */
    @Override
    public UsageAccount currentAccount() {
        QuotaAccount quota = quotaService.getByAccountId(resolveAccountId());
        return toUsageAccount(quota);
    }

    /**
     * 执行resolve账户ID。
     * @return 执行结果
     */
    private Long resolveAccountId() {
        return accountContextResolver.resolveCurrentAccountId();
    }

    /**
     * 执行resolve令牌Amount。
     *
     * @param usageRecord usageRecord
     * @return 执行结果
     */
    private long resolveTokenAmount(UsageRecord usageRecord) {
        if (usageRecord.totalTokenCount() > 0) {
            return usageRecord.totalTokenCount();
        }
        if (usageRecord.estimatedCostAmount() != null
                && usageRecord.estimatedCostAmount().compareTo(BigDecimal.ZERO) > 0) {
            return toTokens(usageRecord.estimatedCostAmount());
        }
        return 1L;
    }

    /**
     * 转换为Tokens。
     *
     * @param costAmount costAmount
     * @return 转换结果
     */
    private long toTokens(BigDecimal costAmount) {
        BigDecimal unit = properties.getCost().getLlmTokenCostAmount();
        if (unit == null || unit.compareTo(BigDecimal.ZERO) <= 0) {
            return costAmount.setScale(0, RoundingMode.CEILING).longValue();
        }
        return costAmount.divide(unit, 0, RoundingMode.CEILING).longValue();
    }

    /**
     * 转换为Usage账户。
     *
     * @param quota 额度
     * @return 转换结果
     */
    private UsageAccount toUsageAccount(QuotaAccount quota) {
        BigDecimal remaining = BigDecimal.valueOf(totalRemaining(quota));
        BigDecimal used = BigDecimal.valueOf(safe(quota.cycleTotal()) + safe(quota.giftTotal())
                + safe(quota.topupTotal()) - totalRemaining(quota));
        return new UsageAccount(
                TenantContext.currentTenantCode(),
                remaining,
                used.max(BigDecimal.ZERO),
                true,
                Instant.now());
    }

    /**
     * 转换为talRemaining。
     *
     * @param quota 额度
     * @return 转换结果
     */
    private long totalRemaining(QuotaAccount quota) {
        return safe(quota.cycleRemaining()) + safe(quota.giftRemaining()) + safe(quota.topupRemaining());
    }

    /**
     * 执行safe。
     *
     * @param value 值
     * @return 执行结果
     */
    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
