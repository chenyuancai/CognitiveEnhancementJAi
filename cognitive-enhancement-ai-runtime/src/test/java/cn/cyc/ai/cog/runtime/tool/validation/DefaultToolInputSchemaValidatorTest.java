package cn.cyc.ai.cog.runtime.tool.validation;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultToolInputSchemaValidatorTest {

    private final DefaultToolInputSchemaValidator validator = new DefaultToolInputSchemaValidator();

    @Test
    void shouldPassWhenRequiredFieldPresent() {
        ToolDefinition tool = sampleTool(requiredQuestionSchema());

        assertDoesNotThrow(() -> validator.validate(Map.of("question", "hello"), tool));
    }

    @Test
    void shouldRejectMissingRequiredField() {
        ToolDefinition tool = sampleTool(requiredQuestionSchema());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validate(Map.of(), tool));
        assertEquals("INVALID_ARGUMENT", ex.getSemanticCode());
    }

    @Test
    void shouldRejectInvalidFieldType() {
        ToolDefinition tool = sampleTool(requiredQuestionSchema());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validate(Map.of("question", 123), tool));
        assertEquals("INVALID_ARGUMENT", ex.getSemanticCode());
    }

    private ToolDefinition sampleTool(SchemaDefinition requestSchema) {
        SchemaDefinition responseSchema = new SchemaDefinition(
                "object", "response", true, Map.of(), null, java.util.List.of()
        );
        return new ToolDefinition(
                "tool.test",
                "测试工具",
                ToolProtocolType.HTTP,
                requestSchema,
                responseSchema,
                "test:invoke",
                RiskLevel.LOW,
                5000,
                new RetryPolicy(1),
                "https://example.com/tool",
                CommonStatus.ENABLED
        );
    }

    private SchemaDefinition requiredQuestionSchema() {
        return new SchemaDefinition(
                "object",
                "request",
                true,
                Map.of("question", new SchemaDefinition(
                        "string", "question", true, Map.of(), null, java.util.List.of()
                )),
                null,
                java.util.List.of()
        );
    }
}
