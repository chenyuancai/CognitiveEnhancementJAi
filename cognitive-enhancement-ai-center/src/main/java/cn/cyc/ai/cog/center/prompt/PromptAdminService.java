package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import org.springframework.stereotype.Service;

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
                request.publishedAt()
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
                definition.publishedAt()
        );
    }
}
