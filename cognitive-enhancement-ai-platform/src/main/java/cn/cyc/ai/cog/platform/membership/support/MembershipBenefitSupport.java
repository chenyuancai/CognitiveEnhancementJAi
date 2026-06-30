package cn.cyc.ai.cog.platform.membership.support;

import cn.cyc.ai.cog.platform.membership.repository.LevelBenefitRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 会员权益解析辅助（优先读 level_benefit 表，回退 benefits_json）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class MembershipBenefitSupport {

    /** 权益MONTHLY令牌。 */
    public static final String BENEFIT_MONTHLY_TOKEN = "usage.monthly_token";
    /** 默认MONTHLY令牌额度。 */
    public static final long DEFAULT_MONTHLY_TOKEN_QUOTA = 100_000L;

    /** 等级权益仓储。 */
    private final LevelBenefitRepository levelBenefitRepository;

    /**
     * 创建会员权益支持工具。
     *
     * @param levelBenefitRepository 等级权益仓储
     */
    public MembershipBenefitSupport(LevelBenefitRepository levelBenefitRepository) {
        this.levelBenefitRepository = levelBenefitRepository;
    }

    /**
     * 解析等级月度 Token 额度（权益目录优先，回退 benefits_json.monthlyTokenK）。
     */
    public long resolveMonthlyTokenQuota(Long levelId, String benefitsJson) {
        if (levelId != null) {
            Long fromCatalog = levelBenefitRepository.findBenefitValue(levelId, BENEFIT_MONTHLY_TOKEN)
                    .map(this::parseLongValue)
                    .orElse(null);
            if (fromCatalog != null && fromCatalog > 0) {
                return fromCatalog;
            }
        }
        Long fromJson = resolveMonthlyTokenFromJson(benefitsJson);
        if (fromJson != null && fromJson > 0) {
            return fromJson;
        }
        return DEFAULT_MONTHLY_TOKEN_QUOTA;
    }

    /**
     * 判断会员等级是否拥有指定权益。
     */
    public boolean hasBenefit(String benefitsJson, String benefitCode) {
        return hasBenefit(null, benefitsJson, benefitCode);
    }

    /**
     * 按等级 ID 解析权益（规范化表优先）。
     */
    public boolean hasBenefit(Long levelId, String benefitsJson, String benefitCode) {
        if (!StringUtils.hasText(benefitCode)) {
            return false;
        }
        if (levelId != null) {
            Boolean fromCatalog = resolveCatalogBoolean(levelId, benefitCode);
            if (fromCatalog != null) {
                return fromCatalog;
            }
        }
        return hasBenefitFromJson(benefitsJson, benefitCode);
    }

    /**
     * 执行resolveCatalogBoolean。
     *
     * @param levelId 等级ID
     * @param benefitCode 权益编码
     * @return 执行结果
     */
    private Boolean resolveCatalogBoolean(Long levelId, String benefitCode) {
        return levelBenefitRepository.findBenefitValue(levelId, benefitCode)
                .map(this::parseBooleanValue)
                .orElse(null);
    }

    /**
     * 执行parseBoolean值。
     *
     * @param value 值
     * @return 执行结果
     */
    private boolean parseBooleanValue(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String trimmed = value.trim();
        if ("true".equalsIgnoreCase(trimmed) || "1".equals(trimmed)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmed) || "0".equals(trimmed)) {
            return false;
        }
        try {
            return Long.parseLong(trimmed) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * 判断是否包含权益FromJSON。
     *
     * @param benefitsJson benefitsJSON
     * @param benefitCode 权益编码
     * @return 是否包含
     */
    private boolean hasBenefitFromJson(String benefitsJson, String benefitCode) {
        if (!StringUtils.hasText(benefitsJson)) {
            return false;
        }
        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(benefitCode) + "\"\\s*:\\s*(true|\"true\"|[1-9]\\d*)",
                Pattern.CASE_INSENSITIVE);
        return pattern.matcher(benefitsJson).find();
    }

    /**
     * 执行resolveMonthly令牌FromJSON。
     *
     * @param benefitsJson benefitsJSON
     * @return 执行结果
     */
    private Long resolveMonthlyTokenFromJson(String benefitsJson) {
        if (!StringUtils.hasText(benefitsJson)) {
            return null;
        }
        Pattern monthlyTokenK = Pattern.compile(
                "\"monthlyTokenK\"\\s*:\\s*([1-9]\\d*)",
                Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = monthlyTokenK.matcher(benefitsJson);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1)) * 1000L;
        }
        Pattern monthlyToken = Pattern.compile(
                "\"" + Pattern.quote(BENEFIT_MONTHLY_TOKEN) + "\"\\s*:\\s*([1-9]\\d*)",
                Pattern.CASE_INSENSITIVE);
        matcher = monthlyToken.matcher(benefitsJson);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    /**
     * 执行parseLong值。
     *
     * @param value 值
     * @return 执行结果
     */
    private Long parseLongValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
