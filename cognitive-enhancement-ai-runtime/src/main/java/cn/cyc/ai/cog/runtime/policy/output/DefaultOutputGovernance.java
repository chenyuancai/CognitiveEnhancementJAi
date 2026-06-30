package cn.cyc.ai.cog.runtime.policy.output;

import cn.cyc.ai.cog.core.harness.OutputGovernance;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * OutputGovernance 默认实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultOutputGovernance implements OutputGovernance {

    /** RISK等级键。 */
    private static final String RISK_LEVEL_KEY = "riskLevel";
    /** NEEDHUMANCONFIRM键。 */
    private static final String NEED_HUMAN_CONFIRM_KEY = "needHumanConfirm";
    /** 邮箱PATTERN。 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);
    /** 手机号PATTERN。 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)(1[3-9]\\d{9})(?!\\d)");

    /** properties。 */
    private final OutputGovernanceProperties properties;

    /**
     * 创建DefaultOutputGovernance。
     *
     * @param properties properties
     */
    public DefaultOutputGovernance(OutputGovernanceProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行govern。
     *
     * @param rawResult raw结果
     * @param outputSchema 输出Schema
     * @return 执行结果
     */
    @Override
    public ExecutionResult govern(ExecutionResult rawResult, Map<String, Object> outputSchema) {
        ExecutionResult result = rawResult;
        if (reviewRequired(outputSchema)) {
            result = appendReviewMetadata(result, outputSchema);
        }
        List<String> violations = detectForbiddenContent(result, outputSchema);
        if (violations.isEmpty()) {
            return result;
        }
        Map<String, Object> output = new LinkedHashMap<>(result.output());
        Map<String, Object> governance = new LinkedHashMap<>();
        Object existing = output.get("governance");
        if (existing instanceof Map<?, ?> existingMap) {
            existingMap.forEach((key, value) -> governance.put(String.valueOf(key), value));
        }
        governance.put("contentViolations", violations);
        governance.put("reviewRequired", true);
        governance.put("reviewStatus", "PENDING_REVIEW");
        output.put("governance", governance);
        return new ExecutionResult(result.status(), result.message(), result.allowedSkillCodes(), output);
    }

    /**
     * 执行sanitizeForLog。
     *
     * @param content 内容
     * @return 执行结果
     */
    @Override
    public String sanitizeForLog(String content) {
        if (content == null || content.isBlank()) {
            return content;
        }
        String sanitized = content.replaceAll("(?i)(apiKey|api_key|password|token)=([^\\s,}]+)", "$1=***");
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("***@***");
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll("1**********");
        return truncateForLog(sanitized);
    }

    /**
     * 执行appendReview元数据。
     *
     * @param rawResult raw结果
     * @param outputSchema 输出Schema
     * @return 执行结果
     */
    private ExecutionResult appendReviewMetadata(ExecutionResult rawResult, Map<String, Object> outputSchema) {
        Map<String, Object> output = new LinkedHashMap<>(rawResult.output());
        output.put("governance", Map.of(
                RISK_LEVEL_KEY, outputSchema.get(RISK_LEVEL_KEY),
                "reviewRequired", true,
                "reviewStatus", "PENDING_REVIEW",
                NEED_HUMAN_CONFIRM_KEY, Boolean.TRUE.equals(outputSchema.get(NEED_HUMAN_CONFIRM_KEY))
        ));
        return new ExecutionResult(
                rawResult.status(),
                rawResult.message(),
                rawResult.allowedSkillCodes(),
                output
        );
    }

    /**
     * 执行detectForbidden内容。
     *
     * @param result 结果
     * @param outputSchema 输出Schema
     * @return 执行结果
     */
    private List<String> detectForbiddenContent(ExecutionResult result, Map<String, Object> outputSchema) {
        String content = extractReviewableContent(result);
        if (content == null || content.isBlank()) {
            return List.of();
        }
        String lowerContent = content.toLowerCase(Locale.ROOT);
        return collectForbiddenKeywords(outputSchema).stream()
                .filter(keyword -> !keyword.isBlank() && lowerContent.contains(keyword.toLowerCase(Locale.ROOT)))
                .distinct()
                .toList();
    }

    /**
     * 执行collectForbiddenKeywords。
     *
     * @param outputSchema 输出Schema
     * @return 执行结果
     */
    private List<String> collectForbiddenKeywords(Map<String, Object> outputSchema) {
        List<String> keywords = new java.util.ArrayList<>(properties.getForbiddenKeywords());
        if (outputSchema != null && outputSchema.get("forbiddenRules") instanceof List<?> rules) {
            rules.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(keywords::add);
        }
        return keywords;
    }

    /**
     * 执行extractReviewable内容。
     *
     * @param result 结果
     * @return 执行结果
     */
    private String extractReviewableContent(ExecutionResult result) {
        if (result == null) {
            return null;
        }
        if (result.message() != null && !result.message().isBlank()) {
            return result.message();
        }
        Object businessOutput = result.output().get("businessOutput");
        return businessOutput == null ? result.output().toString() : businessOutput.toString();
    }

    /**
     * 执行truncateForLog。
     *
     * @param content 内容
     * @return 执行结果
     */
    private String truncateForLog(String content) {
        int maxLength = Math.max(properties.getMaxLogContentLength(), 256);
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...(truncated)";
    }

    /**
     * 执行reviewRequired。
     *
     * @param outputSchema 输出Schema
     * @return 执行结果
     */
    private boolean reviewRequired(Map<String, Object> outputSchema) {
        if (outputSchema == null || outputSchema.isEmpty()) {
            return false;
        }
        return "HIGH".equals(outputSchema.get(RISK_LEVEL_KEY))
                || Boolean.TRUE.equals(outputSchema.get(NEED_HUMAN_CONFIRM_KEY));
    }
}
