package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.audit.spi.AuditRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 管理中心后台元数据 CRUD 服务骨架，统一封装列表、查询、创建、更新与初始化能力。
 *
 * @param <T> 核心定义对象
 * @param <P> 入参对象
 * @param <R> 结果对象
 * @author cyc
 */
public abstract class AbstractMetadataAdminService<T extends MetadataDefinition, P, R> {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractMetadataAdminService.class);

    /**
     * 元数据仓储。
     */
    private final MetadataRepository<T> repository;

    /**
     * 审计记录器。
     */
    private AuditRecorder auditRecorder;

    /**
     * 创建后台管理通用服务。
     *
     * @param repository 元数据仓储
     */
    protected AbstractMetadataAdminService(MetadataRepository<T> repository) {
        this.repository = repository;
    }

    /**
     * 查询当前资源全部定义。
     *
     * @return 列表结果
     */
    public DefinitionListResult<R> listAll() {
        log.info("查询{}全部定义", metadataTypeName());
        List<R> items = repository.listAll().stream()
                .map(this::toResult)
                .toList();
        return new DefinitionListResult<>(items.size(), items);
    }

    /**
     * 按统一 Center 分页契约查询定义。
     *
     * @param query           分页查询参数
     * @param resourceFilter  资源专属过滤器
     * @param statusExtractor 状态提取器
     * @param sorters         允许的排序字段
     * @return 分页结果
     */
    public CenterPageResult<R> listPage(CenterPageQuery query,
                                        Predicate<T> resourceFilter,
                                        Function<T, CommonStatus> statusExtractor,
                                        Map<String, Comparator<T>> sorters) {
        CenterPageQuery normalizedQuery = normalizeQuery(query);
        Comparator<T> comparator = resolveComparator(normalizedQuery, sorters);
        List<T> filteredDefinitions = repository.listAll().stream()
                .filter(definition -> matchesKeyword(definition, normalizedQuery.getKeyword()))
                .filter(definition -> matchesStatus(definition, normalizedQuery.getStatus(), statusExtractor))
                .filter(resourceFilter == null ? definition -> true : resourceFilter)
                .sorted(comparator)
                .toList();
        int total = filteredDefinitions.size();
        int fromIndex = Math.min((normalizedQuery.getPage() - 1) * normalizedQuery.getSize(), total);
        int toIndex = Math.min(fromIndex + normalizedQuery.getSize(), total);
        List<R> items = filteredDefinitions.subList(fromIndex, toIndex).stream()
                .map(this::toResult)
                .toList();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / normalizedQuery.getSize());
        return new CenterPageResult<>(
                total,
                items,
                normalizedQuery.getPage(),
                normalizedQuery.getSize(),
                totalPages,
                normalizedQuery.getPage() < totalPages
        );
    }

    /**
     * 构造通用排序字段。
     *
     * @param statusExtractor 状态提取器
     * @return 通用排序字段
     */
    protected Map<String, Comparator<T>> commonSorters(Function<T, CommonStatus> statusExtractor) {
        return Map.of(
                "code", Comparator.comparing(MetadataDefinition::code),
                "name", Comparator.comparing(MetadataDefinition::name),
                "status", Comparator.comparing(definition -> statusExtractor.apply(definition).name())
        );
    }

    /**
     * 按编码查询定义详情。
     *
     * @param code 资源编码
     * @return 定义详情
     */
    public R getByCode(String code) {
        log.info("查询{}定义详情，code={}", metadataTypeName(), code);
        return toResult(findDefinition(code));
    }

    /**
     * 创建新的定义。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    public R create(P request) {
        T definition = toDefinition(request, null);
        log.info("创建{}定义，code={}", metadataTypeName(), definition.code());
        T saved = repository.save(definition);
        recordConfigChange("CREATE", saved);
        return toResult(saved);
    }

    /**
     * 更新指定编码的定义。
     *
     * @param code    资源编码
     * @param request 更新请求
     * @return 更新结果
     */
    public R update(String code, P request) {
        findDefinition(code);
        T definition = toDefinition(request, code);
        log.info("更新{}定义，code={}", metadataTypeName(), code);
        T saved = repository.save(definition);
        recordConfigChange("UPDATE", saved);
        return toResult(saved);
    }

    /**
     * 在系统初始化阶段写入种子数据。
     *
     * @param request 种子数据请求
     */
    public void seed(P request) {
        T definition = toDefinition(request, null);
        log.info("初始化{}定义，code={}", metadataTypeName(), definition.code());
        T saved = repository.save(definition);
        recordConfigChange("SEED", saved);
    }

    /**
     * 设置审计记录器。
     *
     * @param auditRecorder 审计记录器
     */
    @Autowired(required = false)
    public void setAuditRecorder(AuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
    }

    /**
     * 判断当前资源是否尚未写入任何定义。
     *
     * @return 为 true 表示仓储为空
     */
    public boolean isEmpty() {
        return repository.listAll().isEmpty();
    }

    /**
     * 查询指定编码的定义，不存在时抛出业务异常。
     *
     * @param code 资源编码
     * @return 元数据定义
     */
    protected T findDefinition(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.METADATA_NOT_FOUND, "未找到元数据定义: " + code));
    }

    /**
     * 将请求对象转换为领域定义对象。
     *
     * @param request      请求对象
     * @param overrideCode 覆盖后的资源编码
     * @return 领域定义对象
     */
    protected abstract T toDefinition(P request, String overrideCode);

    /**
     * 将领域定义对象转换为接口返回对象。
     *
     * @param definition 领域定义对象
     * @return 返回对象
     */
    protected abstract R toResult(T definition);

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

    private Comparator<T> resolveComparator(CenterPageQuery query, Map<String, Comparator<T>> sorters) {
        String sortExpression = StringUtils.hasText(query.getSort()) ? query.getSort() : "code,asc";
        String[] parts = sortExpression.split(",");
        String field = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "asc";
        Comparator<T> comparator = sorters.get(field);
        if (comparator == null) {
            throw Errors.of(PlatformErrorCode.RUNTIME_SORT_FIELD_INVALID, "不支持的排序字段: " + field);
        }
        if (!"asc".equals(direction) && !"desc".equals(direction)) {
            throw Errors.of(PlatformErrorCode.RUNTIME_SORT_DIRECTION_INVALID);
        }
        return "desc".equals(direction) ? comparator.reversed() : comparator;
    }

    private boolean matchesKeyword(T definition, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(definition.code(), normalizedKeyword)
                || containsIgnoreCase(definition.name(), normalizedKeyword);
    }

    private boolean matchesStatus(T definition,
                                  CommonStatus status,
                                  Function<T, CommonStatus> statusExtractor) {
        return status == null || status == statusExtractor.apply(definition);
    }

    private boolean containsIgnoreCase(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    private String metadataTypeName() {
        return repository.getClass().getSimpleName();
    }

    private void recordConfigChange(String action, T definition) {
        if (auditRecorder == null) {
            return;
        }
        try {
            auditRecorder.recordConfigChange(action, definition);
        } catch (RuntimeException ex) {
            log.warn("记录配置变更审计失败，action={}, code={}", action, definition.code(), ex);
        }
    }
}
