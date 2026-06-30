package cn.cyc.ai.cog.center.common;

import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Schema DTO 与核心对象转换器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class SchemaDtoMapper {

    /**
     * 创建SchemaDto数据访问 Mapper。
     */
    private SchemaDtoMapper() {
    }

    /**
     * 转换为Dto。
     *
     * @param schemaDefinition SchemaDefinition
     * @return 转换结果
     */
    public static SchemaDto toDto(SchemaDefinition schemaDefinition) {
        if (schemaDefinition == null) {
            return null;
        }
        Map<String, SchemaDto> properties = new LinkedHashMap<>();
        schemaDefinition.properties().forEach((key, value) -> properties.put(key, toDto(value)));
        return new SchemaDto(
                schemaDefinition.type(),
                schemaDefinition.description(),
                schemaDefinition.required(),
                properties,
                toDto(schemaDefinition.items()),
                schemaDefinition.enumValues()
        );
    }

    /**
     * 转换为Domain。
     *
     * @param schemaDto SchemaDto
     * @return 转换结果
     */
    public static SchemaDefinition toDomain(SchemaDto schemaDto) {
        if (schemaDto == null) {
            return null;
        }
        Map<String, SchemaDefinition> properties = new LinkedHashMap<>();
        Map<String, SchemaDto> sourceProperties = schemaDto.properties() == null ? Map.of() : schemaDto.properties();
        sourceProperties.forEach((key, value) -> properties.put(key, toDomain(value)));
        return new SchemaDefinition(
                schemaDto.type(),
                schemaDto.description(),
                schemaDto.required(),
                properties,
                toDomain(schemaDto.items()),
                schemaDto.enumValues()
        );
    }
}
