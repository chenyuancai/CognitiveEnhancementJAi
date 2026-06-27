package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.center.model.catalog.entity.ModelEntity;
import cn.cyc.ai.cog.center.model.catalog.entity.ModelProviderBindingEntity;
import cn.cyc.ai.cog.center.model.catalog.mapper.ModelMapper;
import cn.cyc.ai.cog.center.model.catalog.mapper.ModelProviderBindingMapper;
import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.center.model.provider.ProviderApiKeySupport;
import cn.cyc.ai.cog.center.model.provider.entity.ModelProviderEntity;
import cn.cyc.ai.cog.center.model.provider.mapper.ModelProviderMapper;
import cn.cyc.ai.cog.common.crypto.ApiKeyProtector;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 模型目录数据库仓储。
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbModelCatalogRepository implements ModelCatalogRepository {

    private final ModelProviderMapper providerMapper;
    private final ModelMapper modelMapper;
    private final ModelProviderBindingMapper bindingMapper;
    private final ApiKeyProtector apiKeyProtector;

    public DbModelCatalogRepository(ModelProviderMapper providerMapper,
                                    ModelMapper modelMapper,
                                    ModelProviderBindingMapper bindingMapper,
                                    ApiKeyProtector apiKeyProtector) {
        this.providerMapper = providerMapper;
        this.modelMapper = modelMapper;
        this.bindingMapper = bindingMapper;
        this.apiKeyProtector = apiKeyProtector;
    }

    @Override
    public List<ModelProviderDefinition> listProviders() {
        QueryWrapper<ModelProviderEntity> wrapper = tenantWrapper();
        return providerMapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getProviderCode().compareTo(b.getProviderCode()))
                .map(this::toProviderDefinition)
                .toList();
    }

    @Override
    public Optional<ModelProviderDefinition> findProviderByCode(String providerCode) {
        QueryWrapper<ModelProviderEntity> wrapper = tenantWrapper();
        wrapper.eq("provider_code", providerCode);
        return Optional.ofNullable(providerMapper.selectOne(wrapper)).map(this::toProviderDefinition);
    }

    @Override
    public ModelProviderDefinition saveProvider(ModelProviderDefinition provider) {
        QueryWrapper<ModelProviderEntity> wrapper = tenantWrapper();
        wrapper.eq("provider_code", provider.providerCode());
        ModelProviderEntity existing = providerMapper.selectOne(wrapper);
        ModelProviderEntity entity = toProviderEntity(provider);
        if (existing != null) {
            entity.setId(existing.getId());
            if (!StringUtils.hasText(entity.getApiKey())) {
                entity.setApiKey(existing.getApiKey());
            }
            providerMapper.updateById(entity);
        } else {
            providerMapper.insert(entity);
        }
        return toProviderDefinition(entity);
    }

    @Override
    public boolean providersEmpty() {
        return providerMapper.selectCount(tenantWrapper()) == 0;
    }

    @Override
    public List<ModelMasterDefinition> listModels() {
        QueryWrapper<ModelEntity> wrapper = tenantWrapper();
        return modelMapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getModelCode().compareTo(b.getModelCode()))
                .map(entity -> toModelDefinition(entity, listBindings(entity.getModelCode())))
                .toList();
    }

    @Override
    public Optional<ModelMasterDefinition> findModelByCode(String modelCode) {
        QueryWrapper<ModelEntity> wrapper = tenantWrapper();
        wrapper.eq("model_code", modelCode);
        ModelEntity entity = modelMapper.selectOne(wrapper);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toModelDefinition(entity, listBindings(modelCode)));
    }

    @Override
    @Transactional
    public ModelMasterDefinition saveModel(ModelMasterDefinition model) {
        QueryWrapper<ModelEntity> wrapper = tenantWrapper();
        wrapper.eq("model_code", model.modelCode());
        ModelEntity existing = modelMapper.selectOne(wrapper);
        ModelEntity entity = toModelEntity(model);
        if (existing != null) {
            entity.setId(existing.getId());
            modelMapper.updateById(entity);
        } else {
            modelMapper.insert(entity);
        }
        replaceBindings(model);
        return model;
    }

    @Override
    public boolean modelsEmpty() {
        return modelMapper.selectCount(tenantWrapper()) == 0;
    }

    private void replaceBindings(ModelMasterDefinition model) {
        QueryWrapper<ModelProviderBindingEntity> deleteWrapper = tenantWrapper();
        deleteWrapper.eq("model_code", model.modelCode());
        bindingMapper.delete(deleteWrapper);
        for (ModelBindingDefinition binding : model.providerBindings()) {
            ModelProviderBindingEntity bindingEntity = toBindingEntity(model.modelCode(), binding);
            bindingMapper.insert(bindingEntity);
        }
    }

    private List<ModelBindingDefinition> listBindings(String modelCode) {
        QueryWrapper<ModelProviderBindingEntity> wrapper = tenantWrapper();
        wrapper.eq("model_code", modelCode);
        return bindingMapper.selectList(wrapper).stream()
                .sorted((a, b) -> Integer.compare(b.getRoutePriority(), a.getRoutePriority()))
                .map(this::toBindingDefinition)
                .toList();
    }

    private <T> QueryWrapper<T> tenantWrapper() {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return wrapper;
    }

    private ModelProviderDefinition toProviderDefinition(ModelProviderEntity entity) {
        return new ModelProviderDefinition(
                entity.getProviderCode(),
                entity.getProviderName(),
                entity.getProviderType() == null ? "OPENAI_COMPATIBLE" : entity.getProviderType(),
                entity.getDefaultEndpoint(),
                apiKeyProtector.reveal(entity.getApiKey()),
                entity.getDescription(),
                CommonStatus.valueOf(entity.getStatus())
        );
    }

    private ModelProviderEntity toProviderEntity(ModelProviderDefinition definition) {
        ModelProviderEntity entity = new ModelProviderEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setProviderCode(definition.providerCode());
        entity.setProviderName(definition.providerName());
        entity.setProviderType(definition.providerType());
        entity.setDefaultEndpoint(definition.defaultEndpoint());
        entity.setApiKey(apiKeyProtector.protect(definition.apiKey()));
        entity.setDescription(definition.description());
        entity.setStatus(definition.status().name());
        return entity;
    }

    private ModelMasterDefinition toModelDefinition(ModelEntity entity, List<ModelBindingDefinition> bindings) {
        return new ModelMasterDefinition(
                entity.getModelCode(),
                entity.getModelName(),
                entity.getModelType(),
                entity.getTimeoutMs(),
                entity.getRetryTimes(),
                CommonStatus.valueOf(entity.getStatus()),
                entity.getFallbackModelCode(),
                bindings
        );
    }

    private ModelEntity toModelEntity(ModelMasterDefinition model) {
        ModelEntity entity = new ModelEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setModelCode(model.modelCode());
        entity.setModelName(model.modelName());
        entity.setModelType(model.modelType());
        entity.setTimeoutMs(model.timeoutMs());
        entity.setRetryTimes(model.retryTimes());
        entity.setStatus(model.status().name());
        entity.setFallbackModelCode(model.fallbackModelCode());
        return entity;
    }

    private ModelBindingDefinition toBindingDefinition(ModelProviderBindingEntity entity) {
        String apiKey = apiKeyProtector.reveal(entity.getApiKey());
        if (!ProviderApiKeySupport.isEffectiveApiKey(apiKey)) {
            apiKey = null;
        }
        return new ModelBindingDefinition(
                entity.getModelCode(),
                entity.getProviderCode(),
                entity.getEndpoint(),
                apiKey,
                entity.getRoutePriority() == null ? 0 : entity.getRoutePriority(),
                CommonStatus.valueOf(entity.getStatus())
        );
    }

    private ModelProviderBindingEntity toBindingEntity(String modelCode, ModelBindingDefinition binding) {
        ModelProviderBindingEntity entity = new ModelProviderBindingEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setModelCode(modelCode);
        entity.setProviderCode(binding.providerCode());
        entity.setEndpoint(binding.endpoint());
        entity.setApiKey(apiKeyProtector.protect(binding.apiKey()));
        entity.setRoutePriority(binding.routePriority());
        entity.setStatus(binding.status().name());
        return entity;
    }
}
