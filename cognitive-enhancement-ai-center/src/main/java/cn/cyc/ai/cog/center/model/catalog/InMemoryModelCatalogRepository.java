package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 模型目录内存仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryModelCatalogRepository implements ModelCatalogRepository {

    private final Map<String, ModelProviderDefinition> providers = new LinkedHashMap<>();
    private final Map<String, ModelMasterDefinition> models = new LinkedHashMap<>();

    /**
     * 查询Providers列表。
     * @return 结果列表
     */
    @Override
    public List<ModelProviderDefinition> listProviders() {
        return new ArrayList<>(providers.values());
    }

    /**
     * 查找提供者人编码。
     *
     * @param providerCode 提供者编码
     * @return 查找结果
     */
    @Override
    public Optional<ModelProviderDefinition> findProviderByCode(String providerCode) {
        return Optional.ofNullable(providers.get(providerCode));
    }

    /**
     * 执行save提供者。
     *
     * @param provider 提供者
     * @return 执行结果
     */
    @Override
    public ModelProviderDefinition saveProvider(ModelProviderDefinition provider) {
        providers.put(provider.providerCode(), provider);
        return provider;
    }

    /**
     * 执行providersEmpty。
     * @return 执行结果
     */
    @Override
    public boolean providersEmpty() {
        return providers.isEmpty();
    }

    /**
     * 查询Models列表。
     * @return 结果列表
     */
    @Override
    public List<ModelMasterDefinition> listModels() {
        return new ArrayList<>(models.values());
    }

    /**
     * 查找模型人编码。
     *
     * @param modelCode 模型编码
     * @return 查找结果
     */
    @Override
    public Optional<ModelMasterDefinition> findModelByCode(String modelCode) {
        return Optional.ofNullable(models.get(modelCode));
    }

    /**
     * 执行save模型。
     *
     * @param model 模型
     * @return 执行结果
     */
    @Override
    public ModelMasterDefinition saveModel(ModelMasterDefinition model) {
        models.put(model.modelCode(), model);
        return model;
    }

    /**
     * 执行modelsEmpty。
     * @return 执行结果
     */
    @Override
    public boolean modelsEmpty() {
        return models.isEmpty();
    }
}
