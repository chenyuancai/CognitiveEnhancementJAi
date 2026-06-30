package cn.cyc.ai.cog.platform.quota.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.domain.TokenRecord;
import cn.cyc.ai.cog.platform.quota.repository.QuotaAccountRepository;
import cn.cyc.ai.cog.platform.quota.repository.TokenRecordRepository;
import cn.cyc.ai.cog.platform.support.OperationRecordMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 额度服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class QuotaService {

    /** MAXRETRY。 */
    private static final int MAX_RETRY = 3;

    /** 额度账户仓储。 */
    private final QuotaAccountRepository quotaAccountRepository;
    /** 令牌Record仓储。 */
    private final TokenRecordRepository tokenRecordRepository;
    /** 额度MemberAlloc服务。 */
    private final QuotaMemberAllocService quotaMemberAllocService;

    /**
     * 创建额度服务。
     */
    public QuotaService(QuotaAccountRepository quotaAccountRepository,
                        TokenRecordRepository tokenRecordRepository,
                        QuotaMemberAllocService quotaMemberAllocService) {
        this.quotaAccountRepository = quotaAccountRepository;
        this.tokenRecordRepository = tokenRecordRepository;
        this.quotaMemberAllocService = quotaMemberAllocService;
    }

    /**
     * 获取人账户ID。
     *
     * @param accountId 账户ID
     * @return 人账户ID
     */
    public QuotaAccount getByAccountId(Long accountId) {
        return quotaAccountRepository.requireByAccountId(accountId);
    }

    /**
     * 执行分页令牌Records。
     *
     * @param current current
     * @param size 大小
     * @param accountId 账户ID
     * @return 执行结果
     */
    public PageResult<TokenRecord> pageTokenRecords(long current, long size, Long accountId) {
        return tokenRecordRepository.page(current, size, accountId);
    }

    /**
     * 执行数量AiInvocations。
     *
     * @param tenantId 租户 ID
     * @param start start
     * @param end end
     * @return 执行结果
     */
    public long countAiInvocations(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return tokenRecordRepository.countByTenantAndTypeAndTimeRange(tenantId, "DEDUCT", start, end);
    }

    /**
     * 执行sum令牌Delta。
     *
     * @param tenantId 租户 ID
     * @param start start
     * @param end end
     * @return 执行结果
     */
    public long sumTokenDelta(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return tokenRecordRepository.listByTenantAndTimeRange(tenantId, start, end).stream()
                .mapToLong(record -> record.deltaAmount() == null ? 0L : Math.abs(record.deltaAmount()))
                .sum();
    }

    /**
     * 执行revokeCycle额度。
     *
     * @param accountId 账户ID
     * @param amount amount
     * @param idempotencyKey idempotency键
     * @param remark remark
     * @return 执行结果
     */
    @Transactional
    public QuotaAccount revokeCycleQuota(Long accountId, long amount, String idempotencyKey, String remark) {
        return revokeBucket(accountId, "CYCLE", amount, idempotencyKey, remark);
    }

    /**
     * 执行revokeTopup额度。
     *
     * @param accountId 账户ID
     * @param amount amount
     * @param idempotencyKey idempotency键
     * @param remark remark
     * @return 执行结果
     */
    @Transactional
    public QuotaAccount revokeTopupQuota(Long accountId, long amount, String idempotencyKey, String remark) {
        return revokeBucket(accountId, "TOPUP", amount, idempotencyKey, remark);
    }

    /**
     * 执行revokeBucket。
     * @return 执行结果
     */
    private QuotaAccount revokeBucket(Long accountId, String bucket, long amount,
                                      String idempotencyKey, String remark) {
        if (amount <= 0) {
            return getByAccountId(accountId);
        }
        if (StringUtils.hasText(idempotencyKey) && tokenRecordRepository.findByIdempotencyKey(idempotencyKey) != null) {
            return getByAccountId(accountId);
        }
        QuotaAccount quota = getByAccountId(accountId);
        long revoke = Math.min(amount, bucketRemaining(quota, bucket));
        if (revoke <= 0) {
            return quota;
        }
        quota = applyBucketDelta(quota, bucket, -revoke);
        quotaAccountRepository.update(quota);
        writeRecord(quota, "REVOKE", bucket, -revoke, balanceAfter(quota, bucket), null, "REFUND", null, idempotencyKey, remark);
        return quota;
    }

    /**
     * 执行grantCycle额度。
     *
     * @param accountId 账户ID
     * @param amount amount
     * @param idempotencyKey idempotency键
     * @param remark remark
     * @return 执行结果
     */
    @Transactional
    public QuotaAccount grantCycleQuota(Long accountId, long amount, String idempotencyKey, String remark) {
        return grantBucket(accountId, "CYCLE", amount, idempotencyKey, "GRANT", remark);
    }

    /**
     * 执行grantTopup额度。
     *
     * @param accountId 账户ID
     * @param amount amount
     * @param idempotencyKey idempotency键
     * @param remark remark
     * @return 执行结果
     */
    @Transactional
    public QuotaAccount grantTopupQuota(Long accountId, long amount, String idempotencyKey, String remark) {
        return grantBucket(accountId, "TOPUP", amount, idempotencyKey, "GRANT", remark);
    }

    /**
     * 初始化 FREE 月度 cycle 额度（开户专用，可创建额度账户并写流水）。
     */
    @Transactional
    public QuotaAccount initFreeMonthlyCycle(Long tenantId, Long accountId, long amount,
                                             LocalDateTime cycleResetAt, String idempotencyKey, String remark) {
        if (amount <= 0) {
            throw Errors.of(PlatformErrorCode.QUOTA_OPEN_AMOUNT_INVALID);
        }
        if (StringUtils.hasText(idempotencyKey) && tokenRecordRepository.findByIdempotencyKey(idempotencyKey) != null) {
            QuotaAccount existing = quotaAccountRepository.findByAccountId(accountId);
            if (existing != null) {
                return existing;
            }
        }
        QuotaAccount quota = quotaAccountRepository.findByAccountId(accountId);
        if (quota == null) {
            quotaAccountRepository.insertInitialWithReset(tenantId, accountId, amount, cycleResetAt);
            quota = quotaAccountRepository.requireByAccountId(accountId);
            writeRecord(quota, "GRANT", "CYCLE", amount, amount, null, "ONBOARDING", null, idempotencyKey, remark);
            return quota;
        }
        return grantCycleQuota(accountId, amount, idempotencyKey, remark);
    }

    /**
     * 执行deduct。
     * @return 执行结果
     */
    @Transactional
    public QuotaAccount deduct(Long accountId, long amount, String idempotencyKey,
                               Long memberUserId, String bizType, String bizId) {
        if (amount <= 0) {
            throw Errors.of(PlatformErrorCode.QUOTA_DEDUCT_AMOUNT_INVALID);
        }
        if (StringUtils.hasText(idempotencyKey) && tokenRecordRepository.findByIdempotencyKey(idempotencyKey) != null) {
            return getByAccountId(accountId);
        }
        quotaMemberAllocService.assertMemberQuota(accountId, memberUserId, amount);
        for (int i = 0; i < MAX_RETRY; i++) {
            QuotaAccount quota = getByAccountId(accountId);
            long remaining = amount;
            long cycleUse = Math.min(remaining, safe(quota.cycleRemaining()));
            remaining -= cycleUse;
            long giftUse = Math.min(remaining, safe(quota.giftRemaining()));
            remaining -= giftUse;
            long topupUse = Math.min(remaining, safe(quota.topupRemaining()));
            remaining -= topupUse;
            if (remaining > 0) {
                throw Errors.of(PlatformErrorCode.QUOTA_INSUFFICIENT);
            }
            quota = new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    safe(quota.cycleRemaining()) - cycleUse,
                    quota.cycleTotal(), quota.cycleResetAt(),
                    safe(quota.giftRemaining()) - giftUse,
                    quota.giftTotal(),
                    safe(quota.topupRemaining()) - topupUse,
                    quota.topupTotal()
            );
            int updated = quotaAccountRepository.update(quota);
            if (updated == 0) {
                continue;
            }
            writeRecord(quota, "DEDUCT", "CYCLE", -cycleUse, safe(quota.cycleRemaining()), memberUserId, bizType, bizId, idempotencyKey + ":cycle", null);
            writeRecord(quota, "DEDUCT", "GIFT", -giftUse, safe(quota.giftRemaining()), memberUserId, bizType, bizId, idempotencyKey + ":gift", null);
            writeRecord(quota, "DEDUCT", "TOPUP", -topupUse, safe(quota.topupRemaining()), memberUserId, bizType, bizId, idempotencyKey + ":topup", null);
            quotaMemberAllocService.recordMemberUsage(accountId, memberUserId, amount);
            return quota;
        }
        throw Errors.of(PlatformErrorCode.QUOTA_DEDUCT_CONFLICT);
    }

    /**
     * 执行adjust。
     *
     * @param accountId 账户ID
     * @param bucket bucket
     * @param deltaAmount deltaAmount
     * @param remark remark
     * @return 执行结果
     */
    @Transactional
    public QuotaAccount adjust(Long accountId, String bucket, long deltaAmount, String remark) {
        QuotaAccount quota = getByAccountId(accountId);
        quota = applyBucketDelta(quota, bucket, deltaAmount);
        quotaAccountRepository.update(quota);
        writeRecord(quota, "ADJUST", bucket.toUpperCase(), deltaAmount, balanceAfter(quota, bucket), null, "ADMIN_ADJUST", null, "adjust:" + System.nanoTime(), remark);
        return quota;
    }

    /**
     * 执行grantBucket。
     * @return 执行结果
     */
    private QuotaAccount grantBucket(Long accountId, String bucket, long amount,
                                       String idempotencyKey, String recordType, String remark) {
        if (StringUtils.hasText(idempotencyKey) && tokenRecordRepository.findByIdempotencyKey(idempotencyKey) != null) {
            return getByAccountId(accountId);
        }
        QuotaAccount quota = getByAccountId(accountId);
        quota = switch (bucket) {
            case "CYCLE" -> new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    safe(quota.cycleRemaining()) + amount,
                    safe(quota.cycleTotal()) + amount,
                    quota.cycleResetAt(), quota.giftRemaining(), quota.giftTotal(),
                    quota.topupRemaining(), quota.topupTotal());
            case "TOPUP" -> new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    quota.cycleRemaining(), quota.cycleTotal(), quota.cycleResetAt(),
                    quota.giftRemaining(), quota.giftTotal(),
                    safe(quota.topupRemaining()) + amount,
                    safe(quota.topupTotal()) + amount);
            case "GIFT" -> new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    quota.cycleRemaining(), quota.cycleTotal(), quota.cycleResetAt(),
                    safe(quota.giftRemaining()) + amount,
                    safe(quota.giftTotal()) + amount,
                    quota.topupRemaining(), quota.topupTotal());
            default -> throw Errors.of(PlatformErrorCode.QUOTA_BUCKET_UNKNOWN);
        };
        quotaAccountRepository.update(quota);
        writeRecord(quota, recordType, bucket, amount, balanceAfter(quota, bucket), null, "ORDER", null, idempotencyKey, remark);
        return quota;
    }

    /**
     * 执行applyBucketDelta。
     *
     * @param quota 额度
     * @param bucket bucket
     * @param delta delta
     * @return 执行结果
     */
    private QuotaAccount applyBucketDelta(QuotaAccount quota, String bucket, long delta) {
        return switch (bucket.toUpperCase()) {
            case "CYCLE" -> new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    safe(quota.cycleRemaining()) + delta, quota.cycleTotal(), quota.cycleResetAt(),
                    quota.giftRemaining(), quota.giftTotal(), quota.topupRemaining(), quota.topupTotal());
            case "GIFT" -> new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    quota.cycleRemaining(), quota.cycleTotal(), quota.cycleResetAt(),
                    safe(quota.giftRemaining()) + delta, quota.giftTotal(),
                    quota.topupRemaining(), quota.topupTotal());
            case "TOPUP" -> new QuotaAccount(
                    quota.id(), quota.tenantId(), quota.accountId(),
                    quota.cycleRemaining(), quota.cycleTotal(), quota.cycleResetAt(),
                    quota.giftRemaining(), quota.giftTotal(),
                    safe(quota.topupRemaining()) + delta, quota.topupTotal());
            default -> throw Errors.of(PlatformErrorCode.QUOTA_BUCKET_UNKNOWN, "未知额度桶：" + bucket);
        };
    }

    /**
     * 执行writeRecord。
     */
    private void writeRecord(QuotaAccount quota, String recordType, String bucket, long delta,
                             long balanceAfter, Long memberUserId, String bizType, String bizId,
                             String idempotencyKey, String customMessage) {
        if (delta == 0) {
            return;
        }
        String message = StringUtils.hasText(customMessage)
                ? customMessage.trim()
                : OperationRecordMessages.tokenRecord(recordType, bucket, delta, bizType);
        tokenRecordRepository.insert(new TokenRecord(
                null,
                quota.tenantId(),
                quota.accountId(),
                memberUserId,
                recordType,
                bucket,
                delta,
                balanceAfter,
                bizType,
                bizId,
                idempotencyKey,
                message,
                LocalDateTime.now()
        ));
    }

    /**
     * 执行bucketRemaining。
     *
     * @param quota 额度
     * @param bucket bucket
     * @return 执行结果
     */
    private long bucketRemaining(QuotaAccount quota, String bucket) {
        return switch (bucket.toUpperCase()) {
            case "CYCLE" -> safe(quota.cycleRemaining());
            case "TOPUP" -> safe(quota.topupRemaining());
            case "GIFT" -> safe(quota.giftRemaining());
            default -> 0L;
        };
    }

    /**
     * 执行balanceAfter。
     *
     * @param quota 额度
     * @param bucket bucket
     * @return 执行结果
     */
    private long balanceAfter(QuotaAccount quota, String bucket) {
        return bucketRemaining(quota, bucket);
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
