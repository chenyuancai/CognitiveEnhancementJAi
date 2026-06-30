package cn.cyc.ai.cog.core.metadata.type;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 核心元数据域使用的 Schema 定义对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
