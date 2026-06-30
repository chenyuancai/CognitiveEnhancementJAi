package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.common.SchemaDtoMapper;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;

import java.util.List;

/**
 * Prompt 管理服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class PromptCenterService extends AbstractCenterMetadataService<PromptTemplate> {

    /**
     * 创建提示词Center服务。
     *
     * @param repository 仓储
     */
    public PromptCenterService(PromptTemplateRepository repository) {
        super(repository, "Prompt 模板");
    }

    /**
     * 查询Item列表。
     * @return 结果列表
     */
    public ListResponse<PromptDtos.Result> list() {
        List<PromptDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    /**
     * 执行get。
     *
     * @param promptCode 提示词编码
     * @return 执行结果
     */
    public PromptDtos.Result get(String promptCode) {
        return toResult(getRequired(promptCode));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
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

    /**
     * 更新Item。
     *
     * @param promptCode 提示词编码
     * @param request 请求
     * @return 更新结果
     */
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

    /**
     * 转换为结果。
     *
     * @param template template
     * @return 转换结果
     */
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
