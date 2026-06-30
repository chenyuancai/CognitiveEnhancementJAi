package cn.cyc.ai.cog.runtime.policy;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.PolicyHarness;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;
import cn.cyc.ai.cog.runtime.security.RuntimeRequestSecurityContext;
import cn.cyc.ai.cog.runtime.spi.RuntimeQuotaLimiter;
import cn.cyc.ai.cog.runtime.usage.spi.RuntimeUsageAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * PolicyHarness 默认实现：统一收口 RBAC、额度、限流、灰度租户启停、模型熔断预检与人工确认。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultPolicyHarness implements PolicyHarness {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultPolicyHarness.class);

    /** properties。 */
    private final PolicyHarnessProperties properties;
    /** 请求Security上下文。 */
    private final RuntimeRequestSecurityContext requestSecurityContext;
    /** 运行时额度Limiter。 */
    private final RuntimeQuotaLimiter runtimeQuotaLimiter;
    /** 运行时Usage账户服务。 */
    private final RuntimeUsageAccountService runtimeUsageAccountService;
    /** 智能体Definition仓储。 */
    private final AgentDefinitionRepository agentDefinitionRepository;
    /** 模型Governance。 */
    private final DefaultModelGovernance modelGovernance;

    /**
     * 创建DefaultPolicyHarness。
     */
    public DefaultPolicyHarness(PolicyHarnessProperties properties,
                                RuntimeRequestSecurityContext requestSecurityContext,
                                RuntimeQuotaLimiter runtimeQuotaLimiter,
                                RuntimeUsageAccountService runtimeUsageAccountService,
                                AgentDefinitionRepository agentDefinitionRepository,
                                DefaultModelGovernance modelGovernance) {
        this.properties = properties;
        this.requestSecurityContext = requestSecurityContext;
        this.runtimeQuotaLimiter = runtimeQuotaLimiter;
        this.runtimeUsageAccountService = runtimeUsageAccountService;
        this.agentDefinitionRepository = agentDefinitionRepository;
        this.modelGovernance = modelGovernance;
    }

    /**
     * 执行evaluate。
     *
     * @param capability 能力
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    public PolicyDecision evaluate(CapabilityDefinition capability, ExecutionContext context) {
        assertRoleAllowed(capability);
        runtimeQuotaLimiter.checkAndConsume(capability.capabilityCode());
        runtimeUsageAccountService.checkBeforeExecution(capability.capabilityCode());
        assertModelAvailable(capability, context.traceId());

        boolean needHumanConfirm = capability.needHumanConfirm() || capability.riskLevel() == RiskLevel.HIGH;
        if (needHumanConfirm && !humanConfirmed(context)) {
            return new PolicyDecision(
                    false,
                    "高风险能力需要人工确认",
                    capability.riskLevel().name(),
                    true
            );
        }
        String reason = buildSuccessReason(capability);
        return new PolicyDecision(
                true,
                reason,
                capability.riskLevel().name(),
                needHumanConfirm
        );
    }

    /**
     * 执行assert角色Allowed。
     *
     * @param capability 能力
     */
    private void assertRoleAllowed(CapabilityDefinition capability) {
        if (!properties.isRbacEnabled() || !requestSecurityContext.isAuthEnabled()) {
            return;
        }
        String requiredRole = requiredRole(capability.riskLevel());
        if (!StringUtils.hasText(requiredRole)) {
            return;
        }
        if (requestSecurityContext.hasRole(requiredRole)) {
            return;
        }
        throw new BusinessException("FORBIDDEN",
                "无权执行能力 " + capability.capabilityCode() + "，需要角色: " + requiredRole);
    }

    /**
     * 执行required角色。
     *
     * @param riskLevel risk等级
     * @return 执行结果
     */
    private String requiredRole(RiskLevel riskLevel) {
        if (riskLevel == RiskLevel.HIGH) {
            return properties.getHighRiskRequiredRole();
        }
        if (riskLevel == RiskLevel.MEDIUM && StringUtils.hasText(properties.getMediumRiskRequiredRole())) {
            return properties.getMediumRiskRequiredRole();
        }
        return "";
    }

    /**
     * 执行assert模型Available。
     *
     * @param capability 能力
     * @param traceId 链路 Trace ID
     */
    private void assertModelAvailable(CapabilityDefinition capability, String traceId) {
        if (!properties.isCircuitBreakerPreflightEnabled()) {
            return;
        }
        AgentDefinition agent = agentDefinitionRepository.findByCode(capability.boundAgentCode())
                .orElse(null);
        if (agent == null) {
            return;
        }
        try {
            var resolution = modelGovernance.resolveModel(agent.modelCode());
            if (resolution.fallbackApplied()) {
                log.info("策略预检检测到模型降级, traceId={}, capabilityCode={}, primaryModel={}, actualModel={}, circuitState={}",
                        traceId,
                        capability.capabilityCode(),
                        resolution.primaryModelCode(),
                        resolution.resolvedModelCode(),
                        resolution.circuitState());
            } else if (resolution.circuitState() == ModelCircuitBreakerState.HALF_OPEN) {
                log.info("策略预检检测到模型半开, traceId={}, capabilityCode={}, modelCode={}",
                        traceId, capability.capabilityCode(), agent.modelCode());
            }
        } catch (BusinessException ex) {
            throw ex;
        }
    }

    /**
     * 构建成功原因。
     *
     * @param capability 能力
     * @return 构建结果
     */
    private String buildSuccessReason(CapabilityDefinition capability) {
        if (StringUtils.hasText(capability.version())) {
            return "策略检查通过, version=" + capability.version();
        }
        return "策略检查通过";
    }

    /**
     * 执行humanConfirmed。
     *
     * @param context 上下文
     * @return 执行结果
     */
    private boolean humanConfirmed(ExecutionContext context) {
        Object value = context.request().parameters().get("humanConfirmed");
        return Boolean.TRUE.equals(value);
    }
}
