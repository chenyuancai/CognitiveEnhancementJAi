package cn.cyc.ai.cog.api.schema;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaDefinitionTest {

    @Test
    void shouldPreserveStructuredSchemaProperties() {
        SchemaDefinition stringField = new SchemaDefinition(
                "string",
                "用户问题",
                true,
                Map.of(),
                null,
                List.of()
        );
        SchemaDefinition root = new SchemaDefinition(
                "object",
                "能力输入",
                true,
                Map.of("question", stringField),
                null,
                List.of()
        );

        assertEquals("object", root.type());
        assertEquals("能力输入", root.description());
        assertEquals("string", root.properties().get("question").type());
        assertEquals("用户问题", root.properties().get("question").description());
        assertThrows(UnsupportedOperationException.class, () -> root.properties().put("extra", stringField));
    }
}
