package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.policy.output.DefaultOutputGovernance;
import cn.cyc.ai.cog.runtime.policy.output.OutputGovernanceProperties;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 默认输出治理器测试。
 *
 * @author cyc
 */
class DefaultOutputGovernanceTest {

    @Test
    void shouldMarkHighRiskOutputAsPendingReview() {
        DefaultOutputGovernance governance = governance();
        ExecutionResult rawResult = new ExecutionResult(
                "LLM_GENERATED",
                "ok",
                List.of("skill.chat"),
                Map.of("businessOutput", Map.of("answer", "hello"))
        );

        ExecutionResult result = governance.govern(rawResult, Map.of("riskLevel", "HIGH", "needHumanConfirm", true));

        Map<?, ?> review = (Map<?, ?>) result.output().get("governance");
        assertEquals("HIGH", review.get("riskLevel"));
        assertEquals("PENDING_REVIEW", review.get("reviewStatus"));
        assertEquals(true, review.get("reviewRequired"));
        assertEquals(true, review.get("needHumanConfirm"));
    }

    @Test
    void shouldSanitizeSensitiveContentForLog() {
        DefaultOutputGovernance governance = governance();

        String sanitized = governance.sanitizeForLog(
                "apiKey=sk-test-123 password=secret token=abc email=test@example.com phone=13800138000");

        assertFalse(sanitized.contains("sk-test-123"));
        assertFalse(sanitized.contains("secret"));
        assertFalse(sanitized.contains("test@example.com"));
        assertFalse(sanitized.contains("13800138000"));
    }

    @Test
    void shouldDetectForbiddenContentFromRules() {
        DefaultOutputGovernance governance = governance();
        ExecutionResult rawResult = new ExecutionResult(
                "LLM_GENERATED",
                "回答包含违禁词",
                List.of(),
                Map.of("businessOutput", Map.of("answer", "这是违禁词示例"))
        );

        ExecutionResult result = governance.govern(rawResult, Map.of("forbiddenRules", List.of("违禁词")));

        Map<?, ?> review = (Map<?, ?>) result.output().get("governance");
        assertEquals("PENDING_REVIEW", review.get("reviewStatus"));
        assertTrue(((List<?>) review.get("contentViolations")).contains("违禁词"));
    }

    private DefaultOutputGovernance governance() {
        OutputGovernanceProperties properties = new OutputGovernanceProperties();
        properties.setMaxLogContentLength(2000);
        return new DefaultOutputGovernance(properties);
    }
}
