package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Prompt 模板管理服务。
 *
 * @author cyc
 */
@Service
public class PromptAdminService extends AbstractMetadataAdminService<PromptTemplate, PromptUpsertRequest, PromptResult> {

    /**
     * 创建 Prompt 模板管理服务。
     *
     * @param repository Prompt 模板仓储
     */
    public PromptAdminService(PromptTemplateRepository repository) {
        super(repository);
    }

    /**
     * 分页查询 Prompt 模板。
     *
     * @param query 查询参数
     * @return 分页 Prompt 列表
     */
    public CenterPageResult<PromptResult> listPage(PromptPageQuery query) {
        return listPage(
                query,
                definition -> matches(query.getScenarioCode(), definition.scenarioCode()),
                PromptTemplate::status,
                promptSorters()
        );
    }

    /**
     * 将 Prompt 写入请求转换为模板定义。
     *
     * @param request      Prompt 写入请求
     * @param overrideCode 覆盖编码
     * @return Prompt 模板定义
     */
    @Override
    protected PromptTemplate toDefinition(PromptUpsertRequest request, String overrideCode) {
        String promptCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.promptCode(), "promptCode 不能为空");
        return new PromptTemplate(
                promptCode,
                request.promptName(),
                request.scenarioCode(),
                request.version(),
                request.templateContent(),
                request.variableSchema(),
                request.outputSchema(),
                request.status(),
                request.publishedAt(),
                request.publishedAt() != null ? PromptLifecycleStatus.PUBLISHED : PromptLifecycleStatus.DRAFT
        );
    }

    /**
     * 将 Prompt 模板定义转换为返回对象。
     *
     * @param definition Prompt 模板定义
     * @return Prompt 返回对象
     */
    @Override
    protected PromptResult toResult(PromptTemplate definition) {
        return new PromptResult(
                definition.promptCode(),
                definition.promptName(),
                definition.scenarioCode(),
                definition.version(),
                definition.templateContent(),
                definition.variableSchema(),
                definition.outputSchema(),
                definition.status(),
                definition.lifecycleStatus(),
                definition.publishedAt()
        );
    }

    private Map<String, Comparator<PromptTemplate>> promptSorters() {
        Map<String, Comparator<PromptTemplate>> sorters = new LinkedHashMap<>(commonSorters(PromptTemplate::status));
        sorters.put("scenarioCode", Comparator.comparing(PromptTemplate::scenarioCode));
        sorters.put("version", Comparator.comparing(PromptTemplate::version));
        return sorters;
    }

    private boolean matches(String expected, String actual) {
        return !StringUtils.hasText(expected) || expected.equals(actual);
    }
}
