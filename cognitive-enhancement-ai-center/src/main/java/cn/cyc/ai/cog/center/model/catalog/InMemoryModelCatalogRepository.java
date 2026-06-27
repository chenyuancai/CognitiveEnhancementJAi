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
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false")
public class InMemoryModelCatalogRepository implements ModelCatalogRepository {

    private final Map<String, ModelProviderDefinition> providers = new LinkedHashMap<>();
    private final Map<String, ModelMasterDefinition> models = new LinkedHashMap<>();

    @Override
    public List<ModelProviderDefinition> listProviders() {
        return new ArrayList<>(providers.values());
    }

    @Override
    public Optional<ModelProviderDefinition> findProviderByCode(String providerCode) {
        return Optional.ofNullable(providers.get(providerCode));
    }

    @Override
    public ModelProviderDefinition saveProvider(ModelProviderDefinition provider) {
        providers.put(provider.providerCode(), provider);
        return provider;
    }

    @Override
    public boolean providersEmpty() {
        return providers.isEmpty();
    }

    @Override
    public List<ModelMasterDefinition> listModels() {
        return new ArrayList<>(models.values());
    }

    @Override
    public Optional<ModelMasterDefinition> findModelByCode(String modelCode) {
        return Optional.ofNullable(models.get(modelCode));
    }

    @Override
    public ModelMasterDefinition saveModel(ModelMasterDefinition model) {
        models.put(model.modelCode(), model);
        return model;
    }

    @Override
    public boolean modelsEmpty() {
        return models.isEmpty();
    }
}
