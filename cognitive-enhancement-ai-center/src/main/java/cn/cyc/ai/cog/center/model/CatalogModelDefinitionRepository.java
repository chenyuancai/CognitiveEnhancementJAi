package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.catalog.ModelMasterDefinition;
import cn.cyc.ai.cog.center.model.catalog.ModelRouteResolver;
import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 基于模型目录仓储的模型定义仓储适配器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class CatalogModelDefinitionRepository implements ModelDefinitionRepository {

    /** catalog仓储。 */
    private final ModelCatalogRepository catalogRepository;

    /**
     * 创建Catalog模型Definition仓储。
     *
     * @param catalogRepository catalog仓储
     */
    public CatalogModelDefinitionRepository(ModelCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<ModelDefinition> findByCode(String code) {
        return catalogRepository.findModelByCode(code)
                .flatMap(model -> ModelRouteResolver.selectPrimaryRoute(model, catalogRepository.listProviders()));
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<ModelDefinition> listAll() {
        return ModelRouteResolver.expandRoutes(
                catalogRepository.listModels(),
                catalogRepository.listProviders());
    }

    /**
     * 执行save。
     *
     * @param definition definition
     * @return 执行结果
     */
    @Override
    public ModelDefinition save(ModelDefinition definition) {
        ModelProviderDefinition provider = new ModelProviderDefinition(
                definition.providerCode(),
                definition.providerName(),
                definition.providerType(),
                definition.endpoint(),
                definition.apiKey(),
                null,
                definition.status()
        );
        catalogRepository.saveProvider(provider);

        ModelMasterDefinition existing = catalogRepository.findModelByCode(definition.modelCode()).orElse(null);
        ModelMasterDefinition saved = catalogRepository.saveModel(mergeRoute(existing, definition));
        return ModelRouteResolver.selectPrimaryRoute(saved, catalogRepository.listProviders())
                .orElse(definition);
    }

    /**
     * 执行mergeRoute。
     *
     * @param existing existing
     * @param route route
     * @return 执行结果
     */
    private ModelMasterDefinition mergeRoute(ModelMasterDefinition existing, ModelDefinition route) {
        if (existing == null) {
            return new ModelMasterDefinition(
                    route.modelCode(),
                    route.modelName(),
                    route.modelType(),
                    route.timeoutMs(),
                    route.retryTimes(),
                    route.status(),
                    route.fallbackModelCode(),
                    List.of(new cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition(
                            route.modelCode(),
                            route.providerCode(),
                            route.endpoint(),
                            route.apiKey(),
                            route.routePriority(),
                            route.status()
                    ))
            );
        }
        List<cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition> bindings = existing.providerBindings().stream()
                .map(binding -> binding.providerCode().equals(route.providerCode())
                        ? new cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition(
                        route.modelCode(),
                        route.providerCode(),
                        route.endpoint(),
                        route.apiKey(),
                        route.routePriority(),
                        route.status())
                        : binding)
                .toList();
        boolean exists = bindings.stream().anyMatch(binding -> binding.providerCode().equals(route.providerCode()));
        List<cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition> mergedBindings = exists
                ? bindings
                : appendBinding(bindings, route);
        return new ModelMasterDefinition(
                route.modelCode(),
                route.modelName(),
                route.modelType(),
                route.timeoutMs(),
                route.retryTimes(),
                route.status(),
                route.fallbackModelCode(),
                mergedBindings
        );
    }

    /**
     * 执行appendBinding。
     * @return 执行结果
     */
    private List<cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition> appendBinding(
            List<cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition> bindings,
            ModelDefinition route) {
        List<cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition> merged = new java.util.ArrayList<>(bindings);
        merged.add(new cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition(
                route.modelCode(),
                route.providerCode(),
                route.endpoint(),
                route.apiKey(),
                route.routePriority(),
                route.status()
        ));
        return merged;
    }
}
