package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.PromptResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认 Prompt 解析器。
 *
 * @author cyc
 */
@Service
public class DefaultPromptResolver implements PromptResolver {

    /**
     * Prompt 占位符匹配表达式。
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*([\\w.-]+)\\s*}}");

    /**
     * 解析日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultPromptResolver.class);

    /**
     * Prompt 模板仓储。
     */
    private final PromptTemplateRepository promptTemplateRepository;

    /**
     * 构造默认 Prompt 解析器。
     *
     * @param promptTemplateRepository Prompt 模板仓储
     */
    public DefaultPromptResolver(PromptTemplateRepository promptTemplateRepository) {
        this.promptTemplateRepository = promptTemplateRepository;
    }

    /**
     * 为当前上下文选择 Prompt 模板。
     *
     * @param context 运行时上下文
     * @return Prompt 模板
     */
    @Override
    public PromptTemplate resolve(ExecutionContext context) {
        String scenarioCode = resolveScenarioCode(context.capability().capabilityCode());
        Optional<PromptTemplate> promptTemplate = promptTemplateRepository.listAll().stream()
                .filter(item -> item.status() == CommonStatus.ENABLED)
                .filter(item -> scenarioCode.equals(item.scenarioCode()))
                .max(Comparator.comparing(item -> item.publishedAt() == null ? Instant.EPOCH : item.publishedAt()));
        if (promptTemplate.isPresent()) {
            validateRequiredVariables(promptTemplate.get(), context);
            log.info("已解析 Prompt 模板, traceId={}, capabilityCode={}, scenarioCode={}, promptCode={}",
                    context.traceId(),
                    context.capability().capabilityCode(),
                    scenarioCode,
                    promptTemplate.get().promptCode());
        } else {
            log.info("未解析到 Prompt 模板, traceId={}, capabilityCode={}, scenarioCode={}",
                    context.traceId(), context.capability().capabilityCode(), scenarioCode);
        }
        return promptTemplate.orElse(null);
    }

    /**
     * 渲染 Prompt 文本。
     *
     * @param promptTemplate Prompt 模板
     * @param context        运行时上下文
     * @return 渲染后的 Prompt 文本
     */
    @Override
    public String render(PromptTemplate promptTemplate, ExecutionContext context) {
        if (promptTemplate == null) {
            return null;
        }
        validateRequiredVariables(promptTemplate, context);
        String rendered = promptTemplate.templateContent();
        for (Map.Entry<String, Object> entry : context.request().input().entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        List<String> unresolvedVariables = findUnresolvedVariables(rendered);
        if (!unresolvedVariables.isEmpty()) {
            log.warn("Prompt 渲染失败，存在未替换变量, traceId={}, capabilityCode={}, promptCode={}, unresolvedVariables={}",
                    context.traceId(),
                    context.capability().capabilityCode(),
                    promptTemplate.promptCode(),
                    unresolvedVariables);
            throw new BusinessException("CONFLICT", "Prompt 渲染失败，存在未替换变量: " + String.join(", ", unresolvedVariables));
        }
        return rendered;
    }

    /**
     * 根据能力编码推断场景编码。
     *
     * @param capabilityCode 能力编码
     * @return 场景编码
     */
    private String resolveScenarioCode(String capabilityCode) {
        String[] parts = capabilityCode.split("\\.");
        if (parts.length >= 2) {
            return parts[1];
        }
        return capabilityCode;
    }

    /**
     * 校验 Prompt 所需变量是否已提供。
     *
     * @param promptTemplate Prompt 模板
     * @param context        运行时上下文
     */
    private void validateRequiredVariables(PromptTemplate promptTemplate, ExecutionContext context) {
        SchemaDefinition variableSchema = promptTemplate.variableSchema();
        List<String> missingVariables = variableSchema.properties().entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().required())
                .map(Map.Entry::getKey)
                .filter(variableName -> !context.request().input().containsKey(variableName))
                .sorted()
                .toList();
        if (!missingVariables.isEmpty()) {
            log.warn("Prompt 渲染缺少必要变量, traceId={}, capabilityCode={}, promptCode={}, missingVariables={}",
                    context.traceId(),
                    context.capability().capabilityCode(),
                    promptTemplate.promptCode(),
                    missingVariables);
            throw new BusinessException("CONFLICT", "Prompt 变量缺失: " + String.join(", ", missingVariables));
        }
    }

    /**
     * 查找渲染后仍未替换的变量列表。
     *
     * @param renderedPrompt 渲染后的 Prompt 文本
     * @return 未替换变量列表
     */
    private List<String> findUnresolvedVariables(String renderedPrompt) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(renderedPrompt);
        Set<String> unresolvedVariables = new LinkedHashSet<>();
        while (matcher.find()) {
            unresolvedVariables.add(matcher.group(1));
        }
        return unresolvedVariables.stream().toList();
    }
}
