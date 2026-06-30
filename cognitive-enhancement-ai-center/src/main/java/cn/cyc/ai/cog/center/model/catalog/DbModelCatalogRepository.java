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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 模型目录数据库仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbModelCatalogRepository implements ModelCatalogRepository {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DbModelCatalogRepository.class);

    /** 提供者Mapper。 */
    private final ModelProviderMapper providerMapper;
    /** 模型Mapper。 */
    private final ModelMapper modelMapper;
    /** bindingMapper。 */
    private final ModelProviderBindingMapper bindingMapper;
    /** api键Protector。 */
    private final ApiKeyProtector apiKeyProtector;

    /**
     * 创建Db模型Catalog仓储。
     */
    public DbModelCatalogRepository(ModelProviderMapper providerMapper,
                                    ModelMapper modelMapper,
                                    ModelProviderBindingMapper bindingMapper,
                                    ApiKeyProtector apiKeyProtector) {
        this.providerMapper = providerMapper;
        this.modelMapper = modelMapper;
        this.bindingMapper = bindingMapper;
        this.apiKeyProtector = apiKeyProtector;
    }

    /**
     * 查询Providers列表。
     * @return 结果列表
     */
    @Override
    public List<ModelProviderDefinition> listProviders() {
        QueryWrapper<ModelProviderEntity> wrapper = tenantWrapper();
        return providerMapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getProviderCode().compareTo(b.getProviderCode()))
                .map(this::toProviderDefinition)
                .toList();
    }

    /**
     * 查找提供者人编码。
     *
     * @param providerCode 提供者编码
     * @return 查找结果
     */
    @Override
    public Optional<ModelProviderDefinition> findProviderByCode(String providerCode) {
        QueryWrapper<ModelProviderEntity> wrapper = tenantWrapper();
        wrapper.eq("provider_code", providerCode);
        return Optional.ofNullable(providerMapper.selectOne(wrapper)).map(this::toProviderDefinition);
    }

    /**
     * 执行save提供者。
     *
     * @param provider 提供者
     * @return 执行结果
     */
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

    /**
     * 执行providersEmpty。
     * @return 执行结果
     */
    @Override
    public boolean providersEmpty() {
        return providerMapper.selectCount(tenantWrapper()) == 0;
    }

    /**
     * 查询Models列表。
     * @return 结果列表
     */
    @Override
    public List<ModelMasterDefinition> listModels() {
        QueryWrapper<ModelEntity> wrapper = tenantWrapper();
        return modelMapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getModelCode().compareTo(b.getModelCode()))
                .map(entity -> toModelDefinition(entity, listBindings(entity.getModelCode())))
                .toList();
    }

    /**
     * 查找模型人编码。
     *
     * @param modelCode 模型编码
     * @return 查找结果
     */
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

    /**
     * 执行save模型。
     *
     * @param model 模型
     * @return 执行结果
     */
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

    /**
     * 执行modelsEmpty。
     * @return 执行结果
     */
    @Override
    public boolean modelsEmpty() {
        return modelMapper.selectCount(tenantWrapper()) == 0;
    }

    /**
     * 执行replaceBindings。
     *
     * @param model 模型
     */
    private void replaceBindings(ModelMasterDefinition model) {
        QueryWrapper<ModelProviderBindingEntity> deleteWrapper = tenantWrapper();
        deleteWrapper.eq("model_code", model.modelCode());
        bindingMapper.delete(deleteWrapper);
        for (ModelBindingDefinition binding : model.providerBindings()) {
            ModelProviderBindingEntity bindingEntity = toBindingEntity(model.modelCode(), binding);
            bindingMapper.insert(bindingEntity);
        }
    }

    /**
     * 查询Bindings列表。
     *
     * @param modelCode 模型编码
     * @return 结果列表
     */
    private List<ModelBindingDefinition> listBindings(String modelCode) {
        QueryWrapper<ModelProviderBindingEntity> wrapper = tenantWrapper();
        wrapper.eq("model_code", modelCode);
        return bindingMapper.selectList(wrapper).stream()
                .sorted((a, b) -> Integer.compare(b.getRoutePriority(), a.getRoutePriority()))
                .map(this::toBindingDefinition)
                .toList();
    }

    /**
     * 执行租户Wrapper。
     * @return 执行结果
     */
    private <T> QueryWrapper<T> tenantWrapper() {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return wrapper;
    }

    /**
     * 转换为提供者Definition。
     *
     * @param entity 实体
     * @return 转换结果
     */
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

    /**
     * 转换为提供者实体。
     *
     * @param definition definition
     * @return 转换结果
     */
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

    /**
     * 转换为模型Definition。
     *
     * @param entity 实体
     * @param bindings bindings
     * @return 转换结果
     */
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

    /**
     * 转换为模型实体。
     *
     * @param model 模型
     * @return 转换结果
     */
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

    /**
     * 转换为BindingDefinition。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private ModelBindingDefinition toBindingDefinition(ModelProviderBindingEntity entity) {
        String apiKey = apiKeyProtector.tryReveal(entity.getApiKey());
        if (apiKey == null && StringUtils.hasText(entity.getApiKey())) {
            log.warn("绑定级 apiKey 解密失败，将回退提供商默认 Key, modelCode={}, providerCode={}",
                    entity.getModelCode(), entity.getProviderCode());
        }
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

    /**
     * 转换为Binding实体。
     *
     * @param modelCode 模型编码
     * @param binding binding
     * @return 转换结果
     */
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
