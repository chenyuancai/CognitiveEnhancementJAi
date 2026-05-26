package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.common.SchemaDtoMapper;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;

import java.util.List;

/**
 * Prompt 管理服务。
 */
public class PromptCenterService extends AbstractCenterMetadataService<PromptTemplate> {

    public PromptCenterService(PromptTemplateRepository repository) {
        super(repository, "Prompt 模板");
    }

    public ListResponse<PromptDtos.Result> list() {
        List<PromptDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    public PromptDtos.Result get(String promptCode) {
        return toResult(getRequired(promptCode));
    }

    public PromptDtos.Result create(PromptDtos.CreateRequest request) {
        ensureAbsent(request.promptCode());
        return toResult(save(new PromptTemplate(
                request.promptCode(),
                request.promptName(),
                request.scenarioCode(),
                request.version(),
                request.templateContent(),
                SchemaDtoMapper.toDomain(request.variableSchema()),
                SchemaDtoMapper.toDomain(request.outputSchema()),
                request.status(),
                request.publishedAt()
        )));
    }

    public PromptDtos.Result update(String promptCode, PromptDtos.UpdateRequest request) {
        getRequired(promptCode);
        return toResult(save(new PromptTemplate(
                promptCode,
                request.promptName(),
                request.scenarioCode(),
                request.version(),
                request.templateContent(),
                SchemaDtoMapper.toDomain(request.variableSchema()),
                SchemaDtoMapper.toDomain(request.outputSchema()),
                request.status(),
                request.publishedAt()
        )));
    }

    private PromptDtos.Result toResult(PromptTemplate template) {
        return new PromptDtos.Result(
                template.promptCode(),
                template.promptName(),
                template.scenarioCode(),
                template.version(),
                template.templateContent(),
                SchemaDtoMapper.toDto(template.variableSchema()),
                SchemaDtoMapper.toDto(template.outputSchema()),
                template.status(),
                template.publishedAt()
        );
    }
}
