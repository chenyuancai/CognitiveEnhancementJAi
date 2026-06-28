package cn.cyc.ai.cog.runtime.model.governance;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.coordinator.ExecutionStrategy;
import cn.cyc.ai.cog.runtime.model.registry.LlmRouteRegistry;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultModelGovernanceTest {

    private ModelDefinitionRepository modelDefinitionRepository;
    private ModelCircuitBreakerProperties properties;
    private InMemoryModelCircuitBreakerRegistry circuitBreakerRegistry;
    private DefaultModelGovernance modelGovernance;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantCode("default");
        modelDefinitionRepository = mock(ModelDefinitionRepository.class);
        properties = new ModelCircuitBreakerProperties();
        properties.setEnabled(true);
        properties.setFailureThreshold(2);
        properties.setOpenDurationMs(60_000L);
        circuitBreakerRegistry = new InMemoryModelCircuitBreakerRegistry(Clock.fixed(
                Instant.parse("2026-06-20T04:00:00Z"), ZoneOffset.UTC));
        modelGovernance = new DefaultModelGovernance(
                modelDefinitionRepository, new LlmRouteRegistry(), circuitBreakerRegistry, properties);

        when(modelDefinitionRepository.findByCode("qwen-plus")).thenReturn(Optional.of(primaryModel()));
        when(modelDefinitionRepository.findByCode("gpt-4o-mini")).thenReturn(Optional.of(fallbackModel()));
        when(modelDefinitionRepository.listAll()).thenReturn(List.of(primaryModel(), fallbackModel()));
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldResolvePrimaryWhenCircuitClosed() {
        ModelGovernanceResolution resolution = modelGovernance.resolveModel("qwen-plus");

        assertEquals("qwen-plus", resolution.resolvedModelCode());
        assertFalse(resolution.fallbackApplied());
        assertEquals(ModelCircuitBreakerState.CLOSED, resolution.circuitState());
    }

    @Test
    void shouldFallbackAfterConsecutiveFailuresReachThreshold() {
        modelGovernance.recordFailure("qwen-plus");
        modelGovernance.recordFailure("qwen-plus");

        ModelGovernanceResolution resolution = modelGovernance.resolveModel("qwen-plus");

        assertTrue(resolution.fallbackApplied());
        assertEquals("gpt-4o-mini", resolution.resolvedModelCode());
        assertEquals("qwen-plus", resolution.primaryModelCode());
        assertEquals(ModelCircuitBreakerState.OPEN, resolution.circuitState());
    }

    @Test
    void shouldThrowWhenCircuitOpenWithoutFallback() {
        when(modelDefinitionRepository.findByCode("solo-model"))
                .thenReturn(Optional.of(model("solo-model", null)));

        modelGovernance.recordFailure("solo-model");
        modelGovernance.recordFailure("solo-model");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> modelGovernance.resolveModel("solo-model"));
        assertEquals("MODEL_UNAVAILABLE", exception.getSemanticCode());
    }

    @Test
    void shouldPreferFallbackWhenCostFirstStrategyEnabled() {
        ModelGovernanceResolution resolution = modelGovernance.resolveModel("qwen-plus", ExecutionStrategy.COST_FIRST);

        assertTrue(resolution.fallbackApplied());
        assertEquals("gpt-4o-mini", resolution.resolvedModelCode());
        assertEquals(ModelCircuitBreakerState.CLOSED, resolution.circuitState());
    }

    @Test
    void shouldResetCircuitAfterSuccess() {
        modelGovernance.recordFailure("qwen-plus");
        modelGovernance.recordFailure("qwen-plus");
        assertTrue(modelGovernance.resolveModel("qwen-plus").fallbackApplied());

        modelGovernance.recordSuccess("qwen-plus");
        ModelGovernanceResolution resolution = modelGovernance.resolveModel("qwen-plus");

        assertFalse(resolution.fallbackApplied());
        assertEquals("qwen-plus", resolution.resolvedModelCode());
    }

    private ModelDefinition primaryModel() {
        return model("qwen-plus", "gpt-4o-mini");
    }

    private ModelDefinition fallbackModel() {
        return model("gpt-4o-mini", null);
    }

    private ModelDefinition model(String modelCode, String fallbackModelCode) {
        return new ModelDefinition(
                "bailian",
                "百炼",
                modelCode,
                modelCode,
                "CHAT",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-test-key",
                30_000,
                1,
                CommonStatus.ENABLED,
                10,
                fallbackModelCode
        );
    }
}
