package cn.cyc.ai.cog.runtime.budget;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.support.RuntimeContextParameters;
import cn.cyc.ai.cog.runtime.usage.service.RuntimeUsageProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * 任务级预算控制器：基于 Agent maxCost 或请求参数限制单次执行成本。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class TaskBudgetController {

    /** properties。 */
    private final TaskBudgetProperties properties;
    /** usageProperties。 */
    private final RuntimeUsageProperties usageProperties;
    /** remainingBudget。 */
    private final ThreadLocal<BigDecimal> remainingBudget = new ThreadLocal<>();

    /**
     * 创建TaskBudget接口。
     *
     * @param properties properties
     * @param usageProperties usageProperties
     */
    public TaskBudgetController(TaskBudgetProperties properties, RuntimeUsageProperties usageProperties) {
        this.properties = properties;
        this.usageProperties = usageProperties;
    }

    /**
     * 初始化当前线程预算。
     *
     * @param context 运行时上下文
     * @param agent   Agent 定义
     */
    public void start(ExecutionContext context, AgentDefinition agent) {
        if (!properties.isEnabled()) {
            return;
        }
        remainingBudget.set(resolveBudget(context, agent));
    }

    /**
     * 扣减 Tool 调用成本。
     */
    public void chargeTool() {
        charge(usageProperties.getCost().getToolCallCostAmount());
    }

    /**
     * 按 token 扣减 LLM 成本。
     *
     * @param llmResult LLM 调用结果
     */
    public void chargeLlm(LlmInvocationResult llmResult) {
        int tokenCount = llmResult.totalTokenCount() > 0
                ? llmResult.totalTokenCount()
                : llmResult.inputTokenCount() + llmResult.outputTokenCount();
        if (tokenCount <= 0) {
            tokenCount = 1;
        }
        BigDecimal tokenCost = usageProperties.getCost().getLlmTokenCostAmount()
                .multiply(BigDecimal.valueOf(tokenCount))
                .setScale(6, RoundingMode.HALF_UP);
        charge(tokenCost);
    }

    /**
     * 获取剩余预算。
     *
     * @return 剩余预算
     */
    public Optional<BigDecimal> remaining() {
        return Optional.ofNullable(remainingBudget.get());
    }

    /**
     * 清理当前线程预算。
     */
    public void clear() {
        remainingBudget.remove();
    }

    /**
     * 执行charge。
     *
     * @param amount amount
     */
    private void charge(BigDecimal amount) {
        BigDecimal left = remainingBudget.get();
        if (left == null) {
            return;
        }
        BigDecimal next = left.subtract(amount);
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    ErrorCode.CONFLICT.getCode(),
                    "任务预算不足，剩余: " + left + "，本次消耗: " + amount);
        }
        remainingBudget.set(next);
    }

    /**
     * 执行resolveBudget。
     *
     * @param context 上下文
     * @param agent 智能体
     * @return 执行结果
     */
    private BigDecimal resolveBudget(ExecutionContext context, AgentDefinition agent) {
        Optional<BigDecimal> parameterBudget = RuntimeContextParameters.decimal(context, "taskBudgetAmount");
        if (parameterBudget.isPresent()) {
            return parameterBudget.get();
        }
        return agent.maxCost();
    }
}
