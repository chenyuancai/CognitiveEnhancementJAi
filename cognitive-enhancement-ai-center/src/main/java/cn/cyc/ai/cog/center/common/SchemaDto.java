package cn.cyc.ai.cog.center.common;

import java.util.List;
import java.util.Map;

/**
 * Center 层使用的 Schema DTO。
 *
 * @param type        类型
 * @param description 描述
 * @param required    是否必填
 * @param properties  子属性
 * @param items       数组元素
 * @param enumValues  枚举值
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
