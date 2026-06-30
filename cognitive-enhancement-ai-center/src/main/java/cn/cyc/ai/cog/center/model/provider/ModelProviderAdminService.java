package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.model.registry.ModelRuntimeRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 模型提供商后台管理服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ModelProviderAdminService
        extends AbstractMetadataAdminService<ModelProviderDefinition, ModelProviderUpsertRequest, ModelProviderResult> {

    /** catalog仓储。 */
    private final ModelCatalogRepository catalogRepository;
    /** 模型运行时Refresh服务。 */
    private ModelRuntimeRefreshService modelRuntimeRefreshService;

    /**
     * 创建模型提供者管理后台服务。
     */
    public ModelProviderAdminService(CatalogModelProviderRepository repository,
                                     ModelCatalogRepository catalogRepository) {
        super(repository);
        this.catalogRepository = catalogRepository;
    }

    /**
     * 查询分页列表。
     *
     * @param query 查询
     * @return 结果列表
     */
    public CenterPageResult<ModelProviderResult> listPage(ModelProviderPageQuery query) {
        return listPage(
                query,
                definition -> matches(query.getProviderType(), definition.providerType()),
                ModelProviderDefinition::status,
                providerSorters()
        );
    }

    /**
     * 查询All是否启用列表。
     * @return 结果列表
     */
    public List<ModelProviderResult> listAllEnabled() {
        return catalogRepository.listProviders().stream()
                .filter(provider -> provider.status() == CommonStatus.ENABLED)
                .map(this::toResult)
                .toList();
    }

    /**
     * 判断是否为Empty。
     * @return 是否满足条件
     */
    public boolean isEmpty() {
        return catalogRepository.providersEmpty();
    }

    /**
     * 设置模型运行时Refresh服务。
     *
     * @param modelRuntimeRefreshService 模型运行时Refresh服务
     */
    @Autowired(required = false)
    public void setModelRuntimeRefreshService(ModelRuntimeRefreshService modelRuntimeRefreshService) {
        this.modelRuntimeRefreshService = modelRuntimeRefreshService;
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public ModelProviderResult create(ModelProviderUpsertRequest request) {
        if (!StringUtils.hasText(request.apiKey())) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "创建提供商时 apiKey 不能为空");
        }
        ModelProviderResult result = super.create(request);
        refreshRuntimeRoutes();
        return result;
    }

    /**
     * 更新Item。
     *
     * @param code 编码
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public ModelProviderResult update(String code, ModelProviderUpsertRequest request) {
        ModelProviderDefinition existing = findDefinition(code);
        String apiKey = StringUtils.hasText(request.apiKey()) ? request.apiKey().trim() : existing.apiKey();
        ModelProviderUpsertRequest merged = new ModelProviderUpsertRequest(
                code,
                request.providerName(),
                request.providerType(),
                request.defaultEndpoint(),
                apiKey,
                request.description(),
                request.status()
        );
        ModelProviderResult result = super.update(code, merged);
        refreshRuntimeRoutes();
        return result;
    }

    /**
     * 执行seed。
     *
     * @param request 请求
     */
    @Override
    public void seed(ModelProviderUpsertRequest request) {
        super.seed(request);
        refreshRuntimeRoutes();
    }

    /**
     * 执行refresh运行时Routes。
     */
    private void refreshRuntimeRoutes() {
        if (modelRuntimeRefreshService != null) {
            modelRuntimeRefreshService.refresh();
        }
    }

    /**
     * 转换为Definition。
     *
     * @param request 请求
     * @param overrideCode override编码
     * @return 转换结果
     */
    @Override
    protected ModelProviderDefinition toDefinition(ModelProviderUpsertRequest request, String overrideCode) {
        String providerCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.providerCode(), "providerCode 不能为空");
        return new ModelProviderDefinition(
                providerCode,
                request.providerName(),
                request.providerType() == null ? "OPENAI_COMPATIBLE" : request.providerType(),
                request.defaultEndpoint(),
                request.apiKey(),
                request.description(),
                request.status()
        );
    }

    /**
     * 转换为结果。
     *
     * @param definition definition
     * @return 转换结果
     */
    @Override
    protected ModelProviderResult toResult(ModelProviderDefinition definition) {
        return new ModelProviderResult(
                definition.providerCode(),
                definition.providerName(),
                definition.providerType(),
                definition.defaultEndpoint(),
                ProviderApiKeySupport.isConfigured(definition.apiKey()),
                ProviderApiKeySupport.mask(definition.apiKey()),
                definition.description(),
                definition.status()
        );
    }

    private Map<String, Comparator<ModelProviderDefinition>> providerSorters() {
        Map<String, Comparator<ModelProviderDefinition>> sorters = new LinkedHashMap<>(commonSorters(ModelProviderDefinition::status));
        sorters.put("providerCode", Comparator.comparing(ModelProviderDefinition::providerCode));
        sorters.put("providerType", Comparator.comparing(ModelProviderDefinition::providerType));
        return sorters;
    }

    /**
     * 执行matches。
     *
     * @param expected expected
     * @param actual actual
     * @return 执行结果
     */
    private boolean matches(String expected, String actual) {
        return !StringUtils.hasText(expected) || expected.equals(actual);
    }
}
