package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.model.catalog.ModelBindingDefinition;
import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.catalog.ModelMasterDefinition;
import cn.cyc.ai.cog.center.model.catalog.ModelRouteResolver;
import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.center.model.provider.ProviderApiKeySupport;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.audit.spi.AuditRecorder;
import cn.cyc.ai.cog.runtime.model.registry.ModelRuntimeRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 模型后台管理服务：模型主数据 + 多提供商绑定。
 */
@Service
public class ModelAdminService {

    private final ModelCatalogRepository catalogRepository;
    private AuditRecorder auditRecorder;
    private ModelRuntimeRefreshService modelRuntimeRefreshService;

    public ModelAdminService(ModelCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Autowired(required = false)
    public void setAuditRecorder(AuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
    }

    @Autowired(required = false)
    public void setModelRuntimeRefreshService(ModelRuntimeRefreshService modelRuntimeRefreshService) {
        this.modelRuntimeRefreshService = modelRuntimeRefreshService;
    }

    public CenterPageResult<ModelResult> listPage(ModelPageQuery query) {
        CenterPageQuery normalized = normalizeQuery(query);
        Comparator<ModelMasterDefinition> comparator = resolveComparator(normalized, modelSorters());
        List<ModelMasterDefinition> filtered = catalogRepository.listModels().stream()
                .filter(model -> matchesKeyword(model, normalized.getKeyword()))
                .filter(model -> matchesStatus(model, normalized.getStatus()))
                .filter(model -> matchesProviderCode(model, query.getProviderCode()))
                .filter(model -> matches(query.getModelType(), model.modelType()))
                .sorted(comparator)
                .toList();
        int total = filtered.size();
        int fromIndex = Math.min((normalized.getPage() - 1) * normalized.getSize(), total);
        int toIndex = Math.min(fromIndex + normalized.getSize(), total);
        List<ModelResult> items = filtered.subList(fromIndex, toIndex).stream()
                .map(this::toResult)
                .toList();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / normalized.getSize());
        return new CenterPageResult<>(
                total,
                items,
                normalized.getPage(),
                normalized.getSize(),
                totalPages,
                normalized.getPage() < totalPages
        );
    }

    public ModelResult getByCode(String modelCode) {
        ModelMasterDefinition model = catalogRepository.findModelByCode(modelCode)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.METADATA_NOT_FOUND, "未找到元数据定义: " + modelCode));
        return toResult(model);
    }

    public ModelResult create(ModelUpsertRequest request) {
        ModelMasterDefinition model = toMasterDefinition(request, null);
        validateProvidersExist(model.providerBindings());
        catalogRepository.saveModel(model);
        recordAudit("CREATE", model);
        refreshRuntimeRoutes();
        return toResult(model);
    }

    public ModelResult update(ModelUpsertRequest request) {
        String modelCode = Objects.requireNonNull(request.modelCode(), "modelCode 不能为空");
        catalogRepository.findModelByCode(modelCode)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.METADATA_NOT_FOUND, "未找到元数据定义: " + modelCode));
        ModelMasterDefinition model = toMasterDefinition(request, modelCode);
        validateProvidersExist(model.providerBindings());
        catalogRepository.saveModel(model);
        recordAudit("UPDATE", model);
        refreshRuntimeRoutes();
        return toResult(model);
    }

    public void seed(ModelUpsertRequest request) {
        ModelMasterDefinition model = toMasterDefinition(request, null);
        validateProvidersExist(model.providerBindings());
        catalogRepository.saveModel(model);
        recordAudit("SEED", model);
        refreshRuntimeRoutes();
    }

    public boolean isEmpty() {
        return catalogRepository.modelsEmpty();
    }

    private ModelMasterDefinition toMasterDefinition(ModelUpsertRequest request, String overrideCode) {
        String modelCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.modelCode(), "modelCode 不能为空");
        List<ModelBindingDefinition> bindings = normalizeBindings(request);
        return new ModelMasterDefinition(
                modelCode,
                request.modelName(),
                request.modelType(),
                request.timeoutMs(),
                request.retryTimes(),
                request.status(),
                request.fallbackModelCode(),
                bindings
        );
    }

    private List<ModelBindingDefinition> normalizeBindings(ModelUpsertRequest request) {
        if (!CollectionUtils.isEmpty(request.providerBindings())) {
            return request.providerBindings().stream()
                    .map(binding -> new ModelBindingDefinition(
                            request.modelCode(),
                            Objects.requireNonNull(binding.providerCode(), "providerCode 不能为空"),
                            binding.endpoint(),
                            binding.apiKey(),
                            binding.routePriority() == null ? request.routePriority() : binding.routePriority(),
                            binding.status() == null ? request.status() : binding.status()
                    ))
                    .toList();
        }
        if (!StringUtils.hasText(request.providerCode())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "providerBindings 或 providerCode 至少提供一个");
        }
        return List.of(new ModelBindingDefinition(
                request.modelCode(),
                request.providerCode(),
                request.endpoint(),
                request.apiKey(),
                request.routePriority(),
                request.status()
        ));
    }

    private void validateProvidersExist(List<ModelBindingDefinition> bindings) {
        for (ModelBindingDefinition binding : bindings) {
            catalogRepository.findProviderByCode(binding.providerCode())
                    .orElseThrow(() -> Errors.of(PlatformErrorCode.METADATA_NOT_FOUND,
                            "未找到模型提供商: " + binding.providerCode()));
        }
    }

    private ModelResult toResult(ModelMasterDefinition model) {
        List<ModelProviderDefinition> providers = catalogRepository.listProviders();
        List<ModelProviderBindingResult> bindingResults = model.providerBindings().stream()
                .map(binding -> toBindingResult(binding, providers))
                .toList();
        ModelDefinition primary = ModelRouteResolver.selectPrimaryRoute(model, providers)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.METADATA_NOT_FOUND, "模型未配置有效提供商绑定: " + model.modelCode()));
        return new ModelResult(
                primary.providerCode(),
                primary.providerName(),
                model.modelCode(),
                model.modelName(),
                model.modelType(),
                primary.endpoint(),
                ProviderApiKeySupport.isConfigured(primary.apiKey()),
                ProviderApiKeySupport.mask(primary.apiKey()),
                model.timeoutMs(),
                model.retryTimes(),
                model.status(),
                primary.routePriority(),
                model.fallbackModelCode(),
                bindingResults
        );
    }

    private ModelProviderBindingResult toBindingResult(ModelBindingDefinition binding,
                                                       List<ModelProviderDefinition> providers) {
        ModelProviderDefinition provider = providers.stream()
                .filter(item -> item.providerCode().equals(binding.providerCode()))
                .findFirst()
                .orElseThrow(() -> Errors.of(PlatformErrorCode.METADATA_NOT_FOUND,
                        "未找到模型提供商: " + binding.providerCode()));
        String endpoint = StringUtils.hasText(binding.endpoint()) ? binding.endpoint() : provider.defaultEndpoint();
        String apiKey = ProviderApiKeySupport.isEffectiveApiKey(binding.apiKey()) ? binding.apiKey() : provider.apiKey();
        return new ModelProviderBindingResult(
                provider.providerCode(),
                provider.providerName(),
                provider.providerType(),
                endpoint,
                ProviderApiKeySupport.isConfigured(apiKey),
                ProviderApiKeySupport.mask(apiKey),
                binding.routePriority(),
                binding.status()
        );
    }

    private CenterPageQuery normalizeQuery(CenterPageQuery query) {
        CenterPageQuery normalized = query == null ? new CenterPageQuery() : query;
        if (normalized.getPage() < 1) {
            throw Errors.of(PlatformErrorCode.RUNTIME_PAGE_INVALID);
        }
        if (normalized.getSize() < 1 || normalized.getSize() > 100) {
            throw Errors.of(PlatformErrorCode.RUNTIME_PAGE_SIZE_INVALID);
        }
        return normalized;
    }

    private Comparator<ModelMasterDefinition> resolveComparator(CenterPageQuery query,
                                                                Map<String, Comparator<ModelMasterDefinition>> sorters) {
        String sortExpression = StringUtils.hasText(query.getSort()) ? query.getSort() : "code,asc";
        String[] parts = sortExpression.split(",");
        String field = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "asc";
        Comparator<ModelMasterDefinition> comparator = sorters.get(field);
        if (comparator == null) {
            throw Errors.of(PlatformErrorCode.RUNTIME_SORT_FIELD_INVALID, "不支持的排序字段: " + field);
        }
        if (!"asc".equals(direction) && !"desc".equals(direction)) {
            throw Errors.of(PlatformErrorCode.RUNTIME_SORT_DIRECTION_INVALID);
        }
        return "desc".equals(direction) ? comparator.reversed() : comparator;
    }

    private Map<String, Comparator<ModelMasterDefinition>> modelSorters() {
        Map<String, Comparator<ModelMasterDefinition>> sorters = new LinkedHashMap<>();
        sorters.put("code", Comparator.comparing(ModelMasterDefinition::modelCode));
        sorters.put("name", Comparator.comparing(ModelMasterDefinition::modelName));
        sorters.put("status", Comparator.comparing(model -> model.status().name()));
        sorters.put("modelType", Comparator.comparing(ModelMasterDefinition::modelType));
        return sorters;
    }

    private boolean matchesKeyword(ModelMasterDefinition model, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String normalized = keyword.toLowerCase(Locale.ROOT);
        return model.modelCode().toLowerCase(Locale.ROOT).contains(normalized)
                || model.modelName().toLowerCase(Locale.ROOT).contains(normalized);
    }

    private boolean matchesStatus(ModelMasterDefinition model, CommonStatus status) {
        return status == null || model.status() == status;
    }

    private boolean matchesProviderCode(ModelMasterDefinition model, String providerCode) {
        if (!StringUtils.hasText(providerCode)) {
            return true;
        }
        return model.providerBindings().stream()
                .anyMatch(binding -> providerCode.equals(binding.providerCode()));
    }

    private boolean matches(String expected, String actual) {
        return !StringUtils.hasText(expected) || expected.equals(actual);
    }

    private void recordAudit(String action, ModelMasterDefinition model) {
        if (auditRecorder == null) {
            return;
        }
        ModelDefinition route = ModelRouteResolver.selectPrimaryRoute(model, catalogRepository.listProviders()).orElse(null);
        if (route != null) {
            auditRecorder.recordConfigChange(action, route);
        }
    }

    private void refreshRuntimeRoutes() {
        if (modelRuntimeRefreshService != null) {
            modelRuntimeRefreshService.refresh();
        }
    }
}
