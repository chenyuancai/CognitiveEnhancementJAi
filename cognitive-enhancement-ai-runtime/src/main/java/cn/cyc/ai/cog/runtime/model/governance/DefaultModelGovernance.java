package cn.cyc.ai.cog.runtime.model.governance;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceStateResult;
import cn.cyc.ai.cog.runtime.model.registry.LlmRouteRegistry;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import cn.cyc.ai.cog.runtime.coordinator.ExecutionStrategy;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 默认模型治理：路由解析、降级与熔断。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DefaultModelGovernance {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultModelGovernance.class);

    /** 模型Definition仓储。 */
    private final ModelDefinitionRepository modelDefinitionRepository;
    /** llmRouteRegistry。 */
    private final LlmRouteRegistry llmRouteRegistry;
    /** circuitBreakerRegistry。 */
    private final InMemoryModelCircuitBreakerRegistry circuitBreakerRegistry;
    /** properties。 */
    private final ModelCircuitBreakerProperties properties;

    /**
     * 创建DefaultModelGovernance。
     */
    public DefaultModelGovernance(ModelDefinitionRepository modelDefinitionRepository,
                                  LlmRouteRegistry llmRouteRegistry,
                                  InMemoryModelCircuitBreakerRegistry circuitBreakerRegistry,
                                  ModelCircuitBreakerProperties properties) {
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.llmRouteRegistry = llmRouteRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.properties = properties;
    }

    /**
     * 解析运行时实际调用的模型。
     *
     * @param primaryModelCode 主模型编码
     * @return 治理解析结果
     */
    public ModelGovernanceResolution resolveModel(String primaryModelCode) {
        return resolveModel(primaryModelCode, ExecutionStrategy.BALANCED);
    }

    /**
     * 按执行策略解析运行时实际调用的模型。
     *
     * @param primaryModelCode 主模型编码
     * @param strategy         执行策略
     * @return 治理解析结果
     */
    public ModelGovernanceResolution resolveModel(String primaryModelCode, ExecutionStrategy strategy) {
        if (strategy == ExecutionStrategy.COST_FIRST) {
            return resolveCostFirstModel(primaryModelCode);
        }
        if (strategy == ExecutionStrategy.QUALITY_FIRST) {
            return resolveQualityFirstModel(primaryModelCode);
        }
        return resolveBalancedModel(primaryModelCode);
    }

    /**
     * 执行resolveBalanced模型。
     *
     * @param primaryModelCode primary模型编码
     * @return 执行结果
     */
    private ModelGovernanceResolution resolveBalancedModel(String primaryModelCode) {
        ModelDefinition primary = loadEnabledModel(primaryModelCode);
        if (!properties.isEnabled()) {
            return new ModelGovernanceResolution(
                    primary, primaryModelCode, primary.modelCode(), false, ModelCircuitBreakerState.CLOSED);
        }

        String tenantCode = TenantContext.currentTenantCode();
        InMemoryModelCircuitBreakerRegistry.CircuitBreakerSnapshot snapshot =
                circuitBreakerRegistry.snapshot(tenantCode, primaryModelCode);
        if (snapshot.state() == ModelCircuitBreakerState.OPEN) {
            return resolveFallback(primary, primaryModelCode, snapshot.state());
        }
        return new ModelGovernanceResolution(
                primary,
                primaryModelCode,
                primary.modelCode(),
                false,
                snapshot.state()
        );
    }

    /**
     * 执行resolveQualityFirst模型。
     *
     * @param primaryModelCode primary模型编码
     * @return 执行结果
     */
    private ModelGovernanceResolution resolveQualityFirstModel(String primaryModelCode) {
        ModelDefinition primary = loadEnabledModel(primaryModelCode);
        if (!properties.isEnabled()) {
            return new ModelGovernanceResolution(
                    primary, primaryModelCode, primary.modelCode(), false, ModelCircuitBreakerState.CLOSED);
        }
        String tenantCode = TenantContext.currentTenantCode();
        InMemoryModelCircuitBreakerRegistry.CircuitBreakerSnapshot snapshot =
                circuitBreakerRegistry.snapshot(tenantCode, primaryModelCode);
        if (snapshot.state() == ModelCircuitBreakerState.OPEN) {
            return resolveFallback(primary, primaryModelCode, snapshot.state());
        }
        return new ModelGovernanceResolution(
                primary,
                primaryModelCode,
                primary.modelCode(),
                false,
                snapshot.state()
        );
    }

    /**
     * 执行resolveCostFirst模型。
     *
     * @param primaryModelCode primary模型编码
     * @return 执行结果
     */
    private ModelGovernanceResolution resolveCostFirstModel(String primaryModelCode) {
        ModelDefinition primary = loadEnabledModel(primaryModelCode);
        String fallbackCode = primary.fallbackModelCode();
        if (StringUtils.hasText(fallbackCode)) {
            ModelDefinition fallback = loadEnabledModel(fallbackCode);
            log.info("成本优先策略启用降级模型, primaryModelCode={}, fallbackModelCode={}",
                    primaryModelCode, fallback.modelCode());
            return new ModelGovernanceResolution(
                    fallback,
                    primaryModelCode,
                    fallback.modelCode(),
                    true,
                    ModelCircuitBreakerState.CLOSED
            );
        }
        return resolveBalancedModel(primaryModelCode);
    }

    /**
     * 记录模型调用成功。
     *
     * @param modelCode 实际调用模型编码
     */
    public void recordSuccess(String modelCode) {
        if (!properties.isEnabled() || !StringUtils.hasText(modelCode)) {
            return;
        }
        circuitBreakerRegistry.recordSuccess(TenantContext.currentTenantCode(), modelCode);
    }

    /**
     * 记录模型调用失败。
     *
     * @param modelCode 实际调用模型编码
     */
    public void recordFailure(String modelCode) {
        if (!properties.isEnabled() || !StringUtils.hasText(modelCode)) {
            return;
        }
        circuitBreakerRegistry.recordFailure(
                TenantContext.currentTenantCode(),
                modelCode,
                properties.getFailureThreshold(),
                properties.getOpenDurationMs()
        );
        log.warn("记录模型调用失败, tenantCode={}, modelCode={}, threshold={}",
                TenantContext.currentTenantCode(), modelCode, properties.getFailureThreshold());
    }

    /**
     * 查询当前租户全部模型治理状态。
     *
     * @return 治理状态列表
     */
    public List<ModelGovernanceStateResult> listGovernanceStates() {
        String tenantCode = TenantContext.currentTenantCode();
        return modelDefinitionRepository.listAll().stream()
                .sorted(Comparator.comparingInt(ModelDefinition::routePriority).reversed()
                        .thenComparing(ModelDefinition::modelCode))
                .map(definition -> toStateResult(tenantCode, definition))
                .toList();
    }

    /**
     * 执行resolveFallback。
     * @return 执行结果
     */
    private ModelGovernanceResolution resolveFallback(ModelDefinition primary,
                                                      String primaryModelCode,
                                                      ModelCircuitBreakerState circuitState) {
        String fallbackCode = primary.fallbackModelCode();
        if (!StringUtils.hasText(fallbackCode)) {
            throw new BusinessException("MODEL_UNAVAILABLE", "模型不可用且无降级模型: " + primaryModelCode);
        }
        ModelDefinition fallback = loadEnabledModel(fallbackCode);
        log.info("模型熔断降级, primaryModelCode={}, fallbackModelCode={}, circuitState={}",
                primaryModelCode, fallback.modelCode(), circuitState);
        return new ModelGovernanceResolution(
                fallback,
                primaryModelCode,
                fallback.modelCode(),
                true,
                circuitState
        );
    }

    /**
     * 转换为状态结果。
     *
     * @param tenantCode 租户编码
     * @param definition definition
     * @return 转换结果
     */
    private ModelGovernanceStateResult toStateResult(String tenantCode, ModelDefinition definition) {
        InMemoryModelCircuitBreakerRegistry.CircuitBreakerSnapshot snapshot =
                circuitBreakerRegistry.snapshot(tenantCode, definition.modelCode());
        return new ModelGovernanceStateResult(
                tenantCode,
                definition.modelCode(),
                definition.fallbackModelCode(),
                snapshot.state(),
                snapshot.consecutiveFailureCount(),
                snapshot.openedAt()
        );
    }

    /**
     * 执行load是否启用模型。
     *
     * @param modelCode 模型编码
     * @return 执行结果
     */
    private ModelDefinition loadEnabledModel(String modelCode) {
        ModelDefinition modelDefinition = llmRouteRegistry.findPrimaryRoute(modelCode)
                .or(() -> modelDefinitionRepository.findByCode(modelCode))
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到模型定义: " + modelCode));
        if (modelDefinition.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "模型未启用: " + modelCode);
        }
        return modelDefinition;
    }
}
