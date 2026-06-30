package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 将模型目录仓储适配为提供商元数据仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class CatalogModelProviderRepository implements MetadataRepository<ModelProviderDefinition> {

    /** catalog仓储。 */
    private final ModelCatalogRepository catalogRepository;

    /**
     * 创建Catalog模型提供者仓储。
     *
     * @param catalogRepository catalog仓储
     */
    public CatalogModelProviderRepository(ModelCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<ModelProviderDefinition> findByCode(String code) {
        return catalogRepository.findProviderByCode(code);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<ModelProviderDefinition> listAll() {
        return catalogRepository.listProviders();
    }

    /**
     * 执行save。
     *
     * @param definition definition
     * @return 执行结果
     */
    @Override
    public ModelProviderDefinition save(ModelProviderDefinition definition) {
        return catalogRepository.saveProvider(definition);
    }
}
