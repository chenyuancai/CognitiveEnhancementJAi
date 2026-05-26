package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 模型后台管理服务，负责将后台请求转换为模型定义对象。
 *
 * @author cyc
 */
@Service
public class ModelAdminService extends AbstractMetadataAdminService<ModelDefinition, ModelUpsertRequest, ModelResult> {

    /**
     * 创建模型后台管理服务。
     *
     * @param repository 模型定义仓储
     */
    public ModelAdminService(ModelDefinitionRepository repository) {
        super(repository);
    }

    /**
     * 将后台请求组装为模型定义对象。
     *
     * @param request      模型写入请求
     * @param overrideCode 更新场景下覆盖后的模型编码
     * @return 模型定义对象
     */
    @Override
    protected ModelDefinition toDefinition(ModelUpsertRequest request, String overrideCode) {
        String modelCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.modelCode(), "modelCode 不能为空");
        return new ModelDefinition(
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
        );
    }

    /**
     * 将模型定义转换为后台返回结果。
     *
     * @param definition 模型定义
     * @return 后台返回结果
     */
    @Override
    protected ModelResult toResult(ModelDefinition definition) {
        return new ModelResult(
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
