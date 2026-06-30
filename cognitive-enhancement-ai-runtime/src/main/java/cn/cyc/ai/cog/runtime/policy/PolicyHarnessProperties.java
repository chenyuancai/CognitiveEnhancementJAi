package cn.cyc.ai.cog.runtime.policy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PolicyHarness 策略治理配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.policy")
public class PolicyHarnessProperties {

    /**
     * 是否启用 RBAC 能力访问校验（JWT 鉴权开启时生效）。
     */
    private boolean rbacEnabled = true;

    /**
     * 高风险能力要求的角色。
     */
    private String highRiskRequiredRole = "ADMIN";

    /**
     * 中风险能力要求的角色；为空表示不额外校验。
     */
    private String mediumRiskRequiredRole = "";

    /**
     * 是否在策略阶段预检模型熔断（无降级模型时拒绝执行）。
     */
    private boolean circuitBreakerPreflightEnabled = true;
}
