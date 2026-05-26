package cn.cyc.ai.cog.center.config;

import cn.cyc.ai.cog.center.agent.AgentAdminService;
import cn.cyc.ai.cog.center.agent.AgentUpsertRequest;
import cn.cyc.ai.cog.center.capability.CapabilityAdminService;
import cn.cyc.ai.cog.center.capability.CapabilityUpsertRequest;
import cn.cyc.ai.cog.center.model.ModelAdminService;
import cn.cyc.ai.cog.center.model.ModelUpsertRequest;
import cn.cyc.ai.cog.center.prompt.PromptAdminService;
import cn.cyc.ai.cog.center.prompt.PromptUpsertRequest;
import cn.cyc.ai.cog.center.skill.SkillAdminService;
import cn.cyc.ai.cog.center.skill.SkillUpsertRequest;
import cn.cyc.ai.cog.center.tool.ToolAdminService;
import cn.cyc.ai.cog.center.tool.ToolUpsertRequest;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 管理中心演示数据初始化配置，在应用启动时补齐主链路演示元数据。
 *
 * @author cyc
 */
@Configuration
public class CenterDemoDataInitializer {

    /**
     * 初始化日志。
     */
    private static final Logger log = LoggerFactory.getLogger(CenterDemoDataInitializer.class);

    /**
     * 注册演示数据初始化执行器。
     *
     * @param modelAdminService      模型管理服务
     * @param promptAdminService     提示词管理服务
     * @param capabilityAdminService 能力管理服务
     * @param agentAdminService      Agent 管理服务
     * @param skillAdminService      技能管理服务
     * @param toolAdminService       工具管理服务
     * @return 应用启动执行器
     */
    @Bean
    ApplicationRunner centerSeedRunner(
            ModelAdminService modelAdminService,
            PromptAdminService promptAdminService,
            CapabilityAdminService capabilityAdminService,
            AgentAdminService agentAdminService,
            SkillAdminService skillAdminService,
            ToolAdminService toolAdminService
    ) {
        return arguments -> {
            log.info("开始初始化 Center 演示数据");
            if (modelAdminService.isEmpty()) {
                log.info("初始化模型演示数据");
                modelAdminService.seed(new ModelUpsertRequest(
                        "openai",
                        "OpenAI",
                        "gpt-4o-mini",
                        "GPT-4o Mini",
                        "CHAT",
                        "https://api.openai.com/v1/chat/completions",
                        "credential/openai/default",
                        30_000,
                        2,
                        CommonStatus.ENABLED,
                        10,
                        null
                ));
                modelAdminService.seed(new ModelUpsertRequest(
                        "bailian",
                        "阿里云百炼",
                        "qwen-plus",
                        "Qwen Plus",
                        "CHAT",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1",
                        "env:DASHSCOPE_API_KEY",
                        30_000,
                        2,
                        CommonStatus.ENABLED,
                        20,
                        "gpt-4o-mini"
                ));
            }
            if (promptAdminService.isEmpty()) {
                log.info("初始化提示词模板演示数据");
                promptAdminService.seed(new PromptUpsertRequest(
                        "prompt.qa.default",
                        "默认问答模板",
                        "qa",
                        "v1",
                        "请结合上下文与工具结果回答用户问题：{{question}}",
                        inputSchema(),
                        outputSchema(),
                        CommonStatus.ENABLED,
                        Instant.parse("2026-05-11T00:00:00Z")
                ));
                promptAdminService.seed(new PromptUpsertRequest(
                        "prompt.chat.default",
                        "默认对话模板",
                        "chat",
                        "v1",
                        "请以助手身份直接回答用户问题：{{question}}",
                        inputSchema(),
                        outputSchema(),
                        CommonStatus.ENABLED,
                        Instant.parse("2026-05-11T00:00:00Z")
                ));
            }
            if (toolAdminService.isEmpty()) {
                log.info("初始化工具演示数据");
                toolAdminService.seed(new ToolUpsertRequest(
                        "tool.search",
                        "搜索工具",
                        ToolProtocolType.JAVA_LOCAL,
                        inputSchema(),
                        outputSchema(),
                        "search:query",
                        5_000,
                        1,
                        "demoSearchTool",
                        CommonStatus.ENABLED
                ));
            }
            if (skillAdminService.isEmpty()) {
                log.info("初始化技能演示数据");
                skillAdminService.seed(new SkillUpsertRequest(
                        "skill.qa",
                        "问答技能",
                        "DOMAIN",
                        "优先基于事实回答，不确定时明确说明。",
                        List.of("tool.search"),
                        RiskLevel.LOW,
                        List.of("不得编造来源"),
                        List.of("用户询问事实类问题时可先搜索"),
                        CommonStatus.ENABLED
                ));
                skillAdminService.seed(new SkillUpsertRequest(
                        "skill.chat",
                        "对话技能",
                        "GENERAL",
                        "直接结合提示词完成回答。",
                        List.of(),
                        RiskLevel.LOW,
                        List.of("不得输出攻击性内容"),
                        List.of("适合纯对话与常规生成场景"),
                        CommonStatus.ENABLED
                ));
            }
            if (agentAdminService.isEmpty()) {
                log.info("初始化 Agent 演示数据");
                agentAdminService.seed(new AgentUpsertRequest(
                        "agent.qa",
                        "问答代理",
                        "专业问答助手",
                        "为用户输出可靠答案",
                        "gpt-4o-mini",
                        6,
                        new BigDecimal("1.50"),
                        20_000,
                        List.of("skill.qa"),
                        Map.of(),
                        CommonStatus.ENABLED
                ));
                agentAdminService.seed(new AgentUpsertRequest(
                        "agent.chat",
                        "对话代理",
                        "通用聊天助手",
                        "为用户生成自然语言回答",
                        "gpt-4o-mini",
                        4,
                        new BigDecimal("1.00"),
                        20_000,
                        List.of("skill.chat"),
                        chatAgentParameterConstraints(),
                        CommonStatus.ENABLED
                ));
                agentAdminService.seed(new AgentUpsertRequest(
                        "agent.chat.bailian",
                        "百炼对话代理",
                        "基于阿里云百炼的通用聊天助手",
                        "为用户生成自然语言回答",
                        "qwen-plus",
                        4,
                        new BigDecimal("1.00"),
                        20_000,
                        List.of("skill.chat"),
                        chatAgentParameterConstraints(),
                        CommonStatus.ENABLED
                ));
            }
            if (capabilityAdminService.isEmpty()) {
                log.info("初始化能力演示数据");
                capabilityAdminService.seed(new CapabilityUpsertRequest(
                        "capability.qa.answer",
                        "智能问答",
                        "对外提供基础问答能力",
                        inputSchema(),
                        outputSchema(),
                        qaParameterConstraints(),
                        ExecutionMode.SYNC,
                        "agent.qa",
                        RiskLevel.LOW,
                        false,
                        CommonStatus.ENABLED
                ));
                capabilityAdminService.seed(new CapabilityUpsertRequest(
                        "capability.chat.generate",
                        "智能对话",
                        "对外提供基础对话生成能力",
                        inputSchema(),
                        outputSchema(),
                        chatParameterConstraints(),
                        ExecutionMode.SYNC,
                        "agent.chat",
                        RiskLevel.LOW,
                        false,
                        CommonStatus.ENABLED
                ));
                capabilityAdminService.seed(new CapabilityUpsertRequest(
                        "capability.chat.generate.bailian",
                        "百炼智能对话",
                        "对外提供基于百炼模型的对话生成能力",
                        inputSchema(),
                        outputSchema(),
                        chatParameterConstraints(),
                        ExecutionMode.SYNC,
                        "agent.chat.bailian",
                        RiskLevel.LOW,
                        false,
                        CommonStatus.ENABLED
                ));
            }
            log.info("Center 演示数据初始化完成");
        };
    }

    /**
     * 构造默认输入结构定义。
     *
     * @return 输入 Schema
     */
    private static SchemaDefinition inputSchema() {
        return new SchemaDefinition(
                "object",
                "能力输入",
                true,
                Map.of("question", new SchemaDefinition("string", "用户问题", true, Map.of(), null, List.of())),
                null,
                List.of()
        );
    }

    /**
     * 构造默认输出结构定义。
     *
     * @return 输出 Schema
     */
    private static SchemaDefinition outputSchema() {
        return new SchemaDefinition(
                "object",
                "能力输出",
                true,
                Map.of("answer", new SchemaDefinition("string", "回答内容", true, Map.of(), null, List.of())),
                null,
                List.of()
        );
    }

    /**
     * 构造对话能力的执行参数约束。
     *
     * @return 参数约束映射
     */
    private static Map<String, ParameterConstraintDefinition> chatParameterConstraints() {
        return Map.of(
                "temperature", new ParameterConstraintDefinition("number", false, 0D, 2D, false),
                "topP", new ParameterConstraintDefinition("number", false, 0.01D, 1D, false),
                "maxTokens", new ParameterConstraintDefinition("integer", false, 1D, 8192D, true)
        );
    }

    /**
     * 构造问答能力的执行参数约束。
     *
     * @return 参数约束映射
     */
    private static Map<String, ParameterConstraintDefinition> qaParameterConstraints() {
        return Map.of(
                "temperature", new ParameterConstraintDefinition("number", false, 0D, 1D, false),
                "topP", new ParameterConstraintDefinition("number", false, 0.1D, 1D, false)
        );
    }

    /**
     * 构造对话 Agent 的运行时参数上限。
     *
     * @return 参数约束映射
     */
    private static Map<String, ParameterConstraintDefinition> chatAgentParameterConstraints() {
        return Map.of(
                "temperature", new ParameterConstraintDefinition("number", false, 0D, 1.5D, false),
                "topP", new ParameterConstraintDefinition("number", false, 0.1D, 1D, false),
                "maxTokens", new ParameterConstraintDefinition("integer", false, 1D, 4096D, true)
        );
    }
}
