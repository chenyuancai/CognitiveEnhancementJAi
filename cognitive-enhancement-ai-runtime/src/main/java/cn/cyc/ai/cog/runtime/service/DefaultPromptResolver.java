package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;
import cn.cyc.ai.cog.runtime.file.service.FileProcessingService;
import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;
import cn.cyc.ai.cog.runtime.knowledge.service.KnowledgeRetrievalService;
import cn.cyc.ai.cog.runtime.release.router.PromptReleaseRouter;
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

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*([\\w.-]+)\\s*}}");
    private static final Logger log = LoggerFactory.getLogger(DefaultPromptResolver.class);

    private final PromptTemplateRepository promptTemplateRepository;
    private final PromptReleaseRouter promptReleaseRouter;
    private final KnowledgeRetrievalService knowledgeRetrievalService;
    private final FileProcessingService fileProcessingService;

    public DefaultPromptResolver(PromptTemplateRepository promptTemplateRepository,
                                 PromptReleaseRouter promptReleaseRouter,
                                 KnowledgeRetrievalService knowledgeRetrievalService,
                                 FileProcessingService fileProcessingService) {
        this.promptTemplateRepository = promptTemplateRepository;
        this.promptReleaseRouter = promptReleaseRouter;
        this.knowledgeRetrievalService = knowledgeRetrievalService;
        this.fileProcessingService = fileProcessingService;
    }

    @Override
    public PromptTemplate resolve(ExecutionContext context) {
        String scenarioCode = resolveScenarioCode(context.capability().capabilityCode());
        Optional<PromptTemplate> publishedCandidate = promptTemplateRepository.listAll().stream()
                .filter(PromptTemplate::runtimeVisible)
                .filter(item -> scenarioCode.equals(item.scenarioCode()))
                .max(Comparator.comparing(item -> item.publishedAt() == null ? Instant.EPOCH : item.publishedAt()));
        if (publishedCandidate.isEmpty()) {
            log.info("未解析到 Prompt 模板, traceId={}, capabilityCode={}, scenarioCode={}",
                    context.traceId(), context.capability().capabilityCode(), scenarioCode);
            return null;
        }
        PromptTemplate selected = publishedCandidate.get();
        String resolvedVersion = promptReleaseRouter.resolveVersion(
                selected.promptCode(), context.traceId(), selected.version());
        PromptTemplate resolvedTemplate = promptTemplateRepository
                .findByPromptCodeAndVersion(selected.promptCode(), resolvedVersion)
                .orElse(selected);
        validateRequiredVariables(resolvedTemplate, context);
        log.info("已解析 Prompt 模板, traceId={}, capabilityCode={}, scenarioCode={}, promptCode={}, version={}",
                context.traceId(),
                context.capability().capabilityCode(),
                scenarioCode,
                resolvedTemplate.promptCode(),
                resolvedTemplate.version());
        return resolvedTemplate;
    }

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
        return appendExecutionContext(rendered, context, resolveScenarioCode(context.capability().capabilityCode()));
    }

    private String appendExecutionContext(String rendered, ExecutionContext context, String defaultScenarioCode) {
        StringBuilder builder = new StringBuilder(rendered);
        Map<String, Object> parameters = context.request().parameters();

        if (shouldRetrieveKnowledge(parameters)) {
            String scenarioCode = resolveKnowledgeScenario(parameters, defaultScenarioCode);
            List<KnowledgeFragment> fragments = knowledgeRetrievalService.retrieve(
                    scenarioCode, extractQuery(context), 3);
            if (!fragments.isEmpty()) {
                builder.append("\n\n[Knowledge Context]\n");
                for (KnowledgeFragment fragment : fragments) {
                    builder.append("- ")
                            .append(fragment.title())
                            .append(": ")
                            .append(fragment.content())
                            .append('\n');
                }
                log.info("已注入知识上下文, traceId={}, scenarioCode={}, fragmentCount={}",
                        context.traceId(), scenarioCode, fragments.size());
            }
        }

        Object fileIdValue = parameters.get("fileId");
        if (fileIdValue instanceof String fileId && !fileId.isBlank()) {
            try {
                FileParseTask parseTask = resolveFileParseTask(fileId);
                builder.append("\n\n[File Context]\n").append(parseTask.parseResult());
                log.info("已注入文件解析上下文, traceId={}, fileId={}", context.traceId(), fileId);
            } catch (RuntimeException ex) {
                log.warn("注入文件解析上下文失败, traceId={}, fileId={}, reason={}",
                        context.traceId(), fileId, ex.getMessage());
            }
        }
        return builder.toString();
    }

    private FileParseTask resolveFileParseTask(String fileId) {
        try {
            return fileProcessingService.getLatestParseResult(fileId);
        } catch (BusinessException ex) {
            if (!"NOT_FOUND".equals(ex.getCode())) {
                throw ex;
            }
            fileProcessingService.startParse(fileId);
            return fileProcessingService.getLatestParseResult(fileId);
        }
    }

    private boolean shouldRetrieveKnowledge(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return false;
        }
        if (parameters.containsKey("knowledgeScenario")) {
            return true;
        }
        Object enabled = parameters.get("knowledgeEnabled");
        return Boolean.TRUE.equals(enabled) || "true".equalsIgnoreCase(String.valueOf(enabled));
    }

    private String resolveKnowledgeScenario(Map<String, Object> parameters, String defaultScenarioCode) {
        Object scenario = parameters.get("knowledgeScenario");
        if (scenario != null && !String.valueOf(scenario).isBlank()) {
            return String.valueOf(scenario);
        }
        return defaultScenarioCode;
    }

    private String extractQuery(ExecutionContext context) {
        Object question = context.request().input().get("question");
        if (question != null && !String.valueOf(question).isBlank()) {
            return String.valueOf(question);
        }
        return context.request().input().toString();
    }

    private String resolveScenarioCode(String capabilityCode) {
        String[] parts = capabilityCode.split("\\.");
        if (parts.length >= 2) {
            return parts[1];
        }
        return capabilityCode;
    }

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

    private List<String> findUnresolvedVariables(String renderedPrompt) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(renderedPrompt);
        Set<String> unresolvedVariables = new LinkedHashSet<>();
        while (matcher.find()) {
            unresolvedVariables.add(matcher.group(1));
        }
        return unresolvedVariables.stream().toList();
    }
}
