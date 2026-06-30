package cn.cyc.ai.cog.platform.operations.support;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.operations.domain.MessageTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息模板占位渲染：按 variable_schema 校验必填变量并替换 {{key}}。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class MessageTemplateRenderer {

    /** PLACEHOLDER。 */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)}}");

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建MessageTemplateRenderer。
     *
     * @param objectMapper JSON 序列化器
     */
    public MessageTemplateRenderer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 执行render。
     *
     * @param template template
     * @param params params
     * @return 执行结果
     */
    public String render(MessageTemplate template, Map<String, ?> params) {
        if (template == null) {
            throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_NOT_FOUND);
        }
        Map<String, ?> safeParams = params == null ? Map.of() : params;
        validateRequired(template.variableSchema(), safeParams);
        String content = template.content() == null ? "" : template.content();
        Matcher matcher = PLACEHOLDER.matcher(content);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = safeParams.get(key);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(value == null ? "" : String.valueOf(value)));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 校验参数。
     *
     * @param variableSchema variableSchema
     * @param params params
     */
    private void validateRequired(String variableSchema, Map<String, ?> params) {
        if (!StringUtils.hasText(variableSchema)) {
            return;
        }
        Set<String> required = parseRequiredKeys(variableSchema);
        for (String key : required) {
            if (!params.containsKey(key) || params.get(key) == null) {
                throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_VARIABLE_MISSING, "缺少模板变量：" + key);
            }
            if (params.get(key) instanceof String text && !StringUtils.hasText(text)) {
                throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_VARIABLE_MISSING, "缺少模板变量：" + key);
            }
        }
    }

    /**
     * 执行parseRequiredKeys。
     *
     * @param variableSchema variableSchema
     * @return 执行结果
     */
    private Set<String> parseRequiredKeys(String variableSchema) {
        try {
            List<Map<String, Object>> fields = objectMapper.readValue(variableSchema, new TypeReference<>() {
            });
            Set<String> keys = new LinkedHashSet<>();
            for (Map<String, Object> field : fields) {
                Object name = field.get("name");
                Object required = field.get("required");
                if (name != null && Boolean.TRUE.equals(required)) {
                    keys.add(String.valueOf(name));
                }
            }
            return keys;
        } catch (Exception ex) {
            try {
                List<String> names = objectMapper.readValue(variableSchema, new TypeReference<>() {
                });
                return new LinkedHashSet<>(names);
            } catch (Exception ignored) {
                throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_SCHEMA_INVALID);
            }
        }
    }
}
