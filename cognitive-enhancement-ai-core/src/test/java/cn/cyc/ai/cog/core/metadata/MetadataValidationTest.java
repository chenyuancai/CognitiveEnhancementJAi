package cn.cyc.ai.cog.core.metadata;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MetadataValidationTest {

    private final SchemaDefinition inputSchema = new SchemaDefinition(
            "object",
            "能力输入",
            true,
            Map.of("question", new SchemaDefinition("string", "用户问题", true, Map.of(), null, List.of())),
            null,
            List.of()
    );

    private final SchemaDefinition outputSchema = new SchemaDefinition(
            "object",
            "能力输出",
            true,
            Map.of("answer", new SchemaDefinition("string", "回答内容", true, Map.of(), null, List.of())),
            null,
            List.of()
    );

    @Test
    void shouldRejectNegativeOrZeroRuntimeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> new ModelDefinition(
                "openai", "OpenAI", "gpt-4o-mini", "GPT-4o mini", "CHAT",
                "https://api.openai.com/v1", "openai.default", 0, -1, CommonStatus.ENABLED, -1, null
        ));

        assertThrows(IllegalArgumentException.class, () -> new AgentDefinition(
                "agent.qa", "问答代理", "专业问答助手", "输出准确回答", "gpt-4o-mini",
                0, new BigDecimal("-1.00"), -1, List.of("skill.qa"), Map.of(), CommonStatus.ENABLED
        ));

        assertThrows(IllegalArgumentException.class, () -> new RetryPolicy(-1));

        assertThrows(IllegalArgumentException.class, () -> new ToolDefinition(
                "tool.search", "搜索工具", ToolProtocolType.HTTP, inputSchema, outputSchema,
                "search:query", 0, new RetryPolicy(1), "searchToolAdapter", CommonStatus.ENABLED
        ));
    }

    @Test
    void shouldStillAllowValidMetadataDefinitions() {
        new CapabilityDefinition(
                "qa.answer", "智能问答", "对外提供问答能力", inputSchema, outputSchema,
                Map.of("temperature", new ParameterConstraintDefinition("number", false, 0D, 2D, false)),
                ExecutionMode.SYNC, "agent.qa", RiskLevel.LOW, false, CommonStatus.ENABLED
        );
        new SkillDefinition(
                "skill.qa", "问答技能", "DOMAIN", "优先基于事实回答。",
                List.of("tool.search"), RiskLevel.LOW, List.of("不得编造来源"), List.of("示例"), CommonStatus.ENABLED
        );
    }

    @Test
    void shouldRejectInvalidParameterConstraintRange() {
        assertThrows(IllegalArgumentException.class, () -> new ParameterConstraintDefinition(
                "number", false, 2D, 1D, false
        ));
    }

    @Test
    void shouldAllowAgentDefinitionWithParameterConstraints() {
        new AgentDefinition(
                "agent.chat", "对话代理", "通用聊天助手", "生成自然语言回答", "gpt-4o-mini",
                4, new BigDecimal("1.00"), 20_000, List.of("skill.chat"),
                Map.of("temperature", new ParameterConstraintDefinition("number", false, 0D, 1.5D, false)),
                CommonStatus.ENABLED
        );
    }
}
