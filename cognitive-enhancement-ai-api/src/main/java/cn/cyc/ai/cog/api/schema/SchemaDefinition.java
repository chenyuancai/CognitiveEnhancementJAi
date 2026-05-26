package cn.cyc.ai.cog.api.schema;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 平台通用 Schema 定义对象。
 */
public record SchemaDefinition(
        String type,
        String description,
        boolean required,
        Map<String, SchemaDefinition> properties,
        SchemaDefinition items,
        List<String> enumValues
) {

    public SchemaDefinition {
        type = Objects.requireNonNull(type, "type 不能为空");
        properties = Map.copyOf(properties == null ? Map.of() : properties);
        enumValues = List.copyOf(enumValues == null ? List.of() : enumValues);
    }
}
