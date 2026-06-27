package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;

import java.util.List;
import java.util.Optional;

/**
 * 模型目录仓储：模型主数据 + 提供商绑定 + 提供商主数据。
 */
public interface ModelCatalogRepository {

    List<ModelProviderDefinition> listProviders();

    Optional<ModelProviderDefinition> findProviderByCode(String providerCode);

    ModelProviderDefinition saveProvider(ModelProviderDefinition provider);

    boolean providersEmpty();

    List<ModelMasterDefinition> listModels();

    Optional<ModelMasterDefinition> findModelByCode(String modelCode);

    ModelMasterDefinition saveModel(ModelMasterDefinition model);

    boolean modelsEmpty();
}
