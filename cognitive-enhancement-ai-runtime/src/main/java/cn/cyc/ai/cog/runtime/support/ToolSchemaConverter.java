package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 ToolDefinition / SchemaDefinition 转为 OpenAI function tools 数组项。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class ToolSchemaConverter {

    /**
     * 创建ToolSchemaConverter。
     */
    private ToolSchemaConverter() {
    }

    public static List<Map<String, Object>> toOpenAiTools(List<ToolDefinition> tools) {
        return tools.stream().map(ToolSchemaConverter::toOpenAiTool).toList();
    }

    private static Map<String, Object> toOpenAiTool(ToolDefinition tool) {
        Map<String, Object> function = new LinkedHashMap<>();
        function.put("name", tool.toolCode());
        function.put("description", tool.toolName());
        function.put("parameters", toJsonSchema(tool.requestSchema()));
        Map<String, Object> toolEntry = new LinkedHashMap<>();
        toolEntry.put("type", "function");
        toolEntry.put("function", function);
        return toolEntry;
    }

    private static Map<String, Object> toJsonSchema(SchemaDefinition schema) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("type", schema.type());
        if (schema.description() != null && !schema.description().isBlank()) {
            json.put("description", schema.description());
        }
        if (!schema.properties().isEmpty()) {
            Map<String, Object> properties = new LinkedHashMap<>();
            List<String> required = schema.properties().entrySet().stream()
                    .filter(entry -> entry.getValue().required())
                    .map(Map.Entry::getKey)
                    .toList();
            schema.properties().forEach((key, value) -> properties.put(key, toJsonSchema(value)));
            json.put("properties", properties);
            if (!required.isEmpty()) {
                json.put("required", required);
            }
        }
        if (schema.items() != null) {
            json.put("items", toJsonSchema(schema.items()));
        }
        if (!schema.enumValues().isEmpty()) {
            json.put("enum", schema.enumValues());
        }
        return json;
    }
}
