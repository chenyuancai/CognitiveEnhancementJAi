package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 模型主链路服务，负责对外提供模型定义的查询与维护能力。
 *
 * @author cyc
 */
public class ModelCenterService extends AbstractCenterMetadataService<ModelDefinition> {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ModelCenterService.class);

    /**
     * 创建模型主链路服务。
     *
     * @param repository 模型定义仓储
     */
    public ModelCenterService(ModelDefinitionRepository repository) {
        super(repository, "模型");
    }

    /**
     * 查询全部模型定义。
     *
     * @return 模型列表
     */
    public ListResponse<ModelDtos.Result> list() {
        log.info("主链路查询全部{}定义", resourceLabel());
        List<ModelDtos.Result> items = repository().listAll().stream()
                .map(this::toResult)
                .toList();
        return new ListResponse<>(items, items.size());
    }

    /**
     * 按编码查询模型定义详情。
     *
     * @param modelCode 模型编码
     * @return 模型详情
     */
    public ModelDtos.Result get(String modelCode) {
        log.info("主链路查询{}定义详情，code={}", resourceLabel(), modelCode);
        return toResult(getRequired(modelCode));
    }

    /**
     * 创建模型定义。
     *
     * @param request 模型创建请求
     * @return 创建后的模型定义
     */
    public ModelDtos.Result create(ModelDtos.CreateRequest request) {
        log.info("主链路创建{}定义，code={}", resourceLabel(), request.modelCode());
        ensureAbsent(request.modelCode());
        return toResult(save(new ModelDefinition(
                request.providerCode(),
                request.providerName(),
                request.modelCode(),
                request.modelName(),
                request.modelType(),
                request.endpoint(),
                request.credentialRef(),
                request.timeoutMs(),
                request.retryTimes(),
                request.status(),
                request.routePriority(),
                request.fallbackModelCode()
        )));
    }

    /**
     * 更新指定模型定义。
     *
     * @param modelCode 模型编码
     * @param request   模型更新请求
     * @return 更新后的模型定义
     */
    public ModelDtos.Result update(String modelCode, ModelDtos.UpdateRequest request) {
        log.info("主链路更新{}定义，code={}", resourceLabel(), modelCode);
        getRequired(modelCode);
        return toResult(save(new ModelDefinition(
                request.providerCode(),
                request.providerName(),
                modelCode,
                request.modelName(),
                request.modelType(),
                request.endpoint(),
                request.credentialRef(),
                request.timeoutMs(),
                request.retryTimes(),
                request.status(),
                request.routePriority(),
                request.fallbackModelCode()
        )));
    }

    /**
     * 将模型定义转换为主链路返回结果。
     *
     * @param definition 模型定义
     * @return 主链路返回结果
     */
    private ModelDtos.Result toResult(ModelDefinition definition) {
        return new ModelDtos.Result(
                definition.providerCode(),
                definition.providerName(),
                definition.modelCode(),
                definition.modelName(),
                definition.modelType(),
                definition.endpoint(),
                definition.credentialRef(),
                definition.timeoutMs(),
                definition.retryTimes(),
                definition.status(),
                definition.routePriority(),
                definition.fallbackModelCode()
        );
    }
}
