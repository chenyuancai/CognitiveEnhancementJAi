package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        return toResult(repository.save(definition));
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
        return toResult(repository.save(definition));
    }

    /**
     * 在系统初始化阶段写入种子数据。
     *
     * @param request 种子数据请求
     */
    public void seed(P request) {
        T definition = toDefinition(request, null);
        log.info("初始化{}定义，code={}", metadataTypeName(), definition.code());
        repository.save(definition);
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
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到元数据定义: " + code));
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

    private String metadataTypeName() {
        return repository.getClass().getSimpleName();
    }
}
