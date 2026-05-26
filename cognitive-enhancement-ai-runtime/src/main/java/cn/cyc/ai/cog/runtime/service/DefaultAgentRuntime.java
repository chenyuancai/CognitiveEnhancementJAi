package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.domain.AgentRuntimeResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.AgentRuntime;
import cn.cyc.ai.cog.runtime.spi.ExecutionParameterValidator;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.spi.OutputSchemaValidator;
import cn.cyc.ai.cog.runtime.spi.PromptResolver;
import cn.cyc.ai.cog.runtime.spi.ToolRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 默认 AgentRuntime，实现一期最小装载与 mock 执行。
 *
 * @author cyc
 */
@Service
public class DefaultAgentRuntime implements AgentRuntime {

    /**
     * 运行时日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultAgentRuntime.class);

    /**
     * Agent 定义仓储。
     */
    private final AgentDefinitionRepository agentDefinitionRepository;

    /**
     * Skill 定义仓储。
     */
    private final SkillDefinitionRepository skillDefinitionRepository;

    /**
     * 模型定义仓储。
     */
    private final ModelDefinitionRepository modelDefinitionRepository;

    /**
     * 执行参数校验器。
     */
    private final ExecutionParameterValidator executionParameterValidator;

    /**
     * Tool 运行时。
     */
    private final ToolRuntime toolRuntime;

    /**
     * Prompt 解析器。
     */
    private final PromptResolver promptResolver;

    /**
     * LLM 调用网关。
     */
    private final LlmGateway llmGateway;

    /**
     * 输出 Schema 校验器。
     */
    private final OutputSchemaValidator outputSchemaValidator;

    /**
     * 构造默认 AgentRuntime。
     *
     * @param agentDefinitionRepository Agent 定义仓储
     * @param skillDefinitionRepository Skill 定义仓储
     * @param modelDefinitionRepository 模型定义仓储
     * @param executionParameterValidator 执行参数校验器
     * @param toolRuntime               Tool 运行时
     * @param promptResolver            Prompt 解析器
     * @param llmGateway                LLM 调用网关
     * @param outputSchemaValidator     输出 Schema 校验器
     */
    public DefaultAgentRuntime(AgentDefinitionRepository agentDefinitionRepository,
                               SkillDefinitionRepository skillDefinitionRepository,
                               ModelDefinitionRepository modelDefinitionRepository,
                               ExecutionParameterValidator executionParameterValidator,
                               ToolRuntime toolRuntime,
                               PromptResolver promptResolver,
                               LlmGateway llmGateway,
                               OutputSchemaValidator outputSchemaValidator) {
        this.agentDefinitionRepository = agentDefinitionRepository;
        this.skillDefinitionRepository = skillDefinitionRepository;
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.executionParameterValidator = executionParameterValidator;
        this.toolRuntime = toolRuntime;
        this.promptResolver = promptResolver;
        this.llmGateway = llmGateway;
        this.outputSchemaValidator = outputSchemaValidator;
    }

    /**
     * 执行 Agent 主链路。
     *
     * @param context 运行时上下文
     * @return Agent 执行结果
     */
    @Override
    public AgentRuntimeResult execute(ExecutionContext context) {
        String agentCode = context.capability().boundAgentCode();
        AgentDefinition agent = agentDefinitionRepository.findByCode(agentCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到绑定 Agent: " + agentCode));
        if (agent.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Agent 未启用: " + agentCode);
        }
        executionParameterValidator.validate(context.request(), context.capability(), agent);

        List<SkillDefinition> skills = agent.allowedSkillCodes().stream()
                .map(this::loadEnabledSkill)
                .toList();
        ModelDefinition model = loadEnabledModel(agent.modelCode());
        PromptTemplate promptTemplate = promptResolver.resolve(context);
        ExecutionContext routedContext = context.withAgentPromptAndSkills(agent, promptTemplate, skills);
        log.info("AgentRuntime 已完成基础装载, traceId={}, capabilityCode={}, agentCode={}, modelCode={}, promptCode={}, skillCount={}",
                routedContext.traceId(),
                routedContext.capability().capabilityCode(),
                routedContext.agent().agentCode(),
                model.modelCode(),
                routedContext.prompt() == null ? "NONE" : routedContext.prompt().promptCode(),
                skills.size());

        List<String> boundToolCodes = collectToolCodes(skills);
        ExecutionResult result = boundToolCodes.isEmpty()
                ? buildLlmResult(routedContext, model)
                : buildToolResult(routedContext, boundToolCodes);
        outputSchemaValidator.validate(routedContext.capability(), result);
        return new AgentRuntimeResult(routedContext, result);
    }

    /**
     * 读取并校验启用中的 Skill。
     *
     * @param skillCode Skill 编码
     * @return Skill 定义
     */
    private SkillDefinition loadEnabledSkill(String skillCode) {
        SkillDefinition skill = skillDefinitionRepository.findByCode(skillCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Skill: " + skillCode));
        if (skill.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Skill 未启用: " + skillCode);
        }
        return skill;
    }

    /**
     * 读取并校验启用中的模型定义。
     *
     * @param modelCode 模型编码
     * @return 模型定义
     */
    private ModelDefinition loadEnabledModel(String modelCode) {
        ModelDefinition modelDefinition = modelDefinitionRepository.findByCode(modelCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到模型定义: " + modelCode));
        if (modelDefinition.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "模型未启用: " + modelCode);
        }
        return modelDefinition;
    }

    /**
     * 汇总技能绑定的 Tool 编码。
     *
     * @param skills 已装载技能
     * @return 去重后的 Tool 编码列表
     */
    private List<String> collectToolCodes(List<SkillDefinition> skills) {
        return skills.stream()
                .flatMap(skill -> skill.boundToolCodes().stream())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * 构建 Tool 执行结果。
     *
     * @param context   运行时上下文
     * @param toolCodes 可用 Tool 列表
     * @return 执行结果
     */
    private ExecutionResult buildToolResult(ExecutionContext context, List<String> toolCodes) {
        String selectedToolCode = toolCodes.get(0);
        ToolInvocationResult toolOutput = toolRuntime.invoke(context, selectedToolCode, context.request().input());
        Map<String, Object> output = baseOutput(context);
        output.put("mock", toolOutput.mock());
        output.put("executorType", toolOutput.executorType());
        output.put("toolCodes", toolCodes);
        output.put("invocationResult", toolOutput);
        output.put("toolResult", toolOutput);
        output.put("businessOutput", buildToolBusinessOutput(toolOutput));
        return new ExecutionResult(
                "TOOL_INVOKED",
                "能力已完成路由并触发 mock Tool 执行",
                context.agent().allowedSkillCodes(),
                output
        );
    }

    /**
     * 构建 LLM 执行结果。
     *
     * @param context 运行时上下文
     * @param model   已校验模型定义
     * @return 执行结果
     */
    private ExecutionResult buildLlmResult(ExecutionContext context, ModelDefinition model) {
        Object promptInput = context.prompt() == null
                ? context.request().input()
                : promptResolver.render(context.prompt(), context);
        LlmInvocationResult llmOutput = llmGateway.generate(context, model, promptInput);
        Map<String, Object> output = baseOutput(context);
        output.put("mock", llmOutput.mock());
        output.put("executorType", llmOutput.executorType());
        output.put("modelCode", model.modelCode());
        output.put("providerCode", model.providerCode());
        output.put("invocationResult", llmOutput);
        output.put("llmResult", llmOutput);
        output.put("businessOutput", buildLlmBusinessOutput(llmOutput));
        return new ExecutionResult(
                "LLM_GENERATED",
                "能力已完成路由并触发 LLM 调用",
                context.agent().allowedSkillCodes(),
                output
        );
    }

    /**
     * 构建基础输出对象。
     *
     * @param context 运行时上下文
     * @return 基础输出
     */
    private Map<String, Object> baseOutput(ExecutionContext context) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("mock", true);
        output.put("route", "capability->agent->executor");
        output.put("agentCode", context.agent().agentCode());
        output.put("skillCount", context.skills().size());
        output.put("skillCodes", context.skills().stream().map(SkillDefinition::skillCode).toList());
        output.put("input", context.request().input());
        output.put("parameters", context.request().parameters());
        output.put("prompt", buildPromptPayload(context));
        return output;
    }

    /**
     * 生成 LLM 分支的标准业务输出。
     *
     * @param llmOutput LLM 调用结果
     * @return 业务输出
     */
    private Map<String, Object> buildLlmBusinessOutput(LlmInvocationResult llmOutput) {
        if (llmOutput.answer() == null) {
            return Map.of();
        }
        return Map.of("answer", llmOutput.answer());
    }

    /**
     * 生成 Tool 分支的标准业务输出。
     *
     * @param toolOutput Tool 调用结果
     * @return 业务输出
     */
    private Map<String, Object> buildToolBusinessOutput(ToolInvocationResult toolOutput) {
        if (!(toolOutput.toolPayload() instanceof Map<?, ?> payloadMap)) {
            return Map.of();
        }
        Object answer = payloadMap.get("answer");
        if (answer instanceof String answerText) {
            return Map.of("answer", answerText);
        }
        Object answerPreview = payloadMap.get("answerPreview");
        if (answerPreview instanceof String previewText) {
            return Map.of("answer", previewText);
        }
        return Map.of();
    }

    /**
     * 构建 Prompt 摘要信息。
     *
     * @param context 运行时上下文
     * @return Prompt 摘要
     */
    private Map<String, Object> buildPromptPayload(ExecutionContext context) {
        if (context.prompt() == null) {
            return Map.of("resolved", false);
        }
        return Map.of(
                "resolved", true,
                "promptCode", context.prompt().promptCode(),
                "scenarioCode", context.prompt().scenarioCode(),
                "version", context.prompt().version(),
                "renderedPrompt", promptResolver.render(context.prompt(), context)
        );
    }
}
