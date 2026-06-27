package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 将模型目录仓储适配为提供商元数据仓储。
 */
@Repository
public class CatalogModelProviderRepository implements MetadataRepository<ModelProviderDefinition> {

    private final ModelCatalogRepository catalogRepository;

    public CatalogModelProviderRepository(ModelCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public Optional<ModelProviderDefinition> findByCode(String code) {
        return catalogRepository.findProviderByCode(code);
    }

    @Override
    public List<ModelProviderDefinition> listAll() {
        return catalogRepository.listProviders();
    }

    @Override
    public ModelProviderDefinition save(ModelProviderDefinition definition) {
        return catalogRepository.saveProvider(definition);
    }
}
