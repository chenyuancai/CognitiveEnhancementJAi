package cn.cyc.ai.cog.runtime.policy;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.PolicyHarness;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;
import cn.cyc.ai.cog.runtime.security.RuntimeRequestSecurityContext;
import cn.cyc.ai.cog.runtime.spi.RuntimeQuotaLimiter;
import cn.cyc.ai.cog.runtime.usage.spi.RuntimeUsageAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 默认策略治理器测试。
 *
 * @author cyc
 */
class DefaultPolicyHarnessTest {

    private PolicyHarnessProperties properties;
    private RuntimeRequestSecurityContext requestSecurityContext;
    private RuntimeQuotaLimiter runtimeQuotaLimiter;
    private RuntimeUsageAccountService runtimeUsageAccountService;
    private AgentDefinitionRepository agentDefinitionRepository;
    private DefaultModelGovernance modelGovernance;
    private DefaultPolicyHarness harness;

    @BeforeEach
    void setUp() {
        properties = new PolicyHarnessProperties();
        requestSecurityContext = mock(RuntimeRequestSecurityContext.class);
        runtimeQuotaLimiter = mock(RuntimeQuotaLimiter.class);
        runtimeUsageAccountService = mock(RuntimeUsageAccountService.class);
        agentDefinitionRepository = mock(AgentDefinitionRepository.class);
        modelGovernance = mock(DefaultModelGovernance.class);
        harness = new DefaultPolicyHarness(
                properties,
                requestSecurityContext,
                runtimeQuotaLimiter,
                runtimeUsageAccountService,
                agentDefinitionRepository,
                modelGovernance
        );
        when(requestSecurityContext.isAuthEnabled()).thenReturn(false);
        when(agentDefinitionRepository.findByCode("agent.chat")).thenReturn(Optional.of(sampleAgent()));
        when(modelGovernance.resolveModel("gpt-4o-mini")).thenReturn(sampleResolution(false));
    }

    @Test
    void shouldRejectHighRiskCapabilityWhenHumanConfirmMissing() {
        CapabilityDefinition capability = capability(RiskLevel.HIGH, true);
        ExecutionContext context = context(capability, Map.of());

        PolicyHarness.PolicyDecision decision = harness.evaluate(capability, context);

        assertFalse(decision.allowed());
        assertTrue(decision.needHumanConfirm());
    }

    @Test
    void shouldAllowHighRiskCapabilityWhenHumanConfirmed() {
        CapabilityDefinition capability = capability(RiskLevel.HIGH, true);
        ExecutionContext context = context(capability, Map.of("humanConfirmed", true));

        PolicyHarness.PolicyDecision decision = harness.evaluate(capability, context);

        assertTrue(decision.allowed());
        verify(runtimeQuotaLimiter).checkAndConsume(capability.capabilityCode());
        verify(runtimeUsageAccountService).checkBeforeExecution(capability.capabilityCode());
    }

    @Test
    void shouldRejectWhenRbacRoleMissing() {
        properties.setRbacEnabled(true);
        when(requestSecurityContext.isAuthEnabled()).thenReturn(true);
        when(requestSecurityContext.hasRole("ADMIN")).thenReturn(false);

        CapabilityDefinition capability = capability(RiskLevel.HIGH, false);
        ExecutionContext context = context(capability, Map.of("humanConfirmed", true));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> harness.evaluate(capability, context));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldPropagateQuotaExceeded() {
        doThrow(new BusinessException("TOO_MANY_REQUESTS", "quota exceeded"))
                .when(runtimeQuotaLimiter).checkAndConsume(anyString());

        CapabilityDefinition capability = capability(RiskLevel.LOW, false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> harness.evaluate(capability, context(capability, Map.of())));

        assertEquals("TOO_MANY_REQUESTS", exception.getSemanticCode());
    }

    @Test
    void shouldPropagateModelUnavailableWhenCircuitOpenWithoutFallback() {
        when(modelGovernance.resolveModel("gpt-4o-mini"))
                .thenThrow(new BusinessException("MODEL_UNAVAILABLE", "模型不可用且无降级模型: gpt-4o-mini"));

        CapabilityDefinition capability = capability(RiskLevel.LOW, false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> harness.evaluate(capability, context(capability, Map.of())));

        assertEquals("MODEL_UNAVAILABLE", exception.getSemanticCode());
    }

    @Test
    void shouldAllowWhenModelFallsBackDuringPreflight() {
        when(modelGovernance.resolveModel("gpt-4o-mini"))
                .thenReturn(sampleResolution(true));

        CapabilityDefinition capability = capability(RiskLevel.LOW, false);

        PolicyHarness.PolicyDecision decision = harness.evaluate(capability, context(capability, Map.of()));

        assertTrue(decision.allowed());
    }

    private ExecutionContext context(CapabilityDefinition capability, Map<String, Object> parameters) {
        return new ExecutionContext(
                "trace-policy",
                new CapabilityExecuteRequest(capability.capabilityCode(), Map.of("question", "hello"), parameters),
                capability,
                null,
                null,
                List.of(),
                Map.of()
        );
    }

    private CapabilityDefinition capability(RiskLevel riskLevel, boolean needHumanConfirm) {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        return new CapabilityDefinition(
                "capability.high-risk",
                "高风险能力",
                "测试高风险能力",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.chat",
                riskLevel,
                needHumanConfirm,
                CommonStatus.ENABLED
        );
    }

    private AgentDefinition sampleAgent() {
        return new AgentDefinition(
                "agent.chat",
                "对话 Agent",
                "role",
                "goal",
                "gpt-4o-mini",
                4,
                BigDecimal.ONE,
                20000,
                List.of("skill.chat"),
                Map.of(),
                CommonStatus.ENABLED
        );
    }

    private ModelGovernanceResolution sampleResolution(boolean fallbackApplied) {
        ModelDefinition model = new ModelDefinition(
                "openai",
                "OpenAI",
                "gpt-4o-mini",
                "GPT",
                "chat",
                "http://localhost",
                null,
                5000,
                1,
                CommonStatus.ENABLED,
                0,
                fallbackApplied ? "gpt-fallback" : null
        );
        return new ModelGovernanceResolution(
                model,
                "gpt-4o-mini",
                fallbackApplied ? "gpt-fallback" : "gpt-4o-mini",
                fallbackApplied,
                fallbackApplied ? ModelCircuitBreakerState.OPEN : ModelCircuitBreakerState.CLOSED
        );
    }
}
