package cn.cyc.ai.cog.center.common;

import java.util.List;
import java.util.Map;

/**
 * Center 层使用的 Schema DTO。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record SchemaDto(
        String type,
        String description,
        boolean required,
        Map<String, SchemaDto> properties,
        SchemaDto items,
        List<String> enumValues
) {
}
