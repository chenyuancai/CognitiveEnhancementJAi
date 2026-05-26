package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Center 模块面向主链路元数据的通用服务基类，封装查询校验与持久化入口。
 *
 * @param <T> 元数据类型
 * @author cyc
 */
public abstract class AbstractCenterMetadataService<T extends MetadataDefinition> {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractCenterMetadataService.class);

    /**
     * 底层元数据仓储。
     */
    private final MetadataRepository<T> repository;

    /**
     * 资源中文标签，用于日志与异常提示。
     */
    private final String resourceLabel;

    /**
     * 创建 Center 通用服务基类。
     *
     * @param repository    元数据仓储
     * @param resourceLabel 资源中文标签
     */
    protected AbstractCenterMetadataService(MetadataRepository<T> repository, String resourceLabel) {
        this.repository = repository;
        this.resourceLabel = resourceLabel;
    }

    /**
     * 返回当前服务绑定的元数据仓储。
     *
     * @return 元数据仓储
     */
    protected MetadataRepository<T> repository() {
        return repository;
    }

    /**
     * 返回当前服务对应的资源标签。
     *
     * @return 资源标签
     */
    protected String resourceLabel() {
        return resourceLabel;
    }

    /**
     * 按编码获取必填资源，不存在时抛出业务异常。
     *
     * @param code 资源编码
     * @return 已存在的资源定义
     */
    protected T getRequired(String code) {
        log.debug("查询{}定义详情，code={}", resourceLabel, code);
        return repository.findByCode(code)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", resourceLabel + "不存在: " + code));
    }

    /**
     * 校验资源编码未被占用。
     *
     * @param code 待创建资源编码
     */
    protected void ensureAbsent(String code) {
        log.debug("校验{}定义编码是否可用，code={}", resourceLabel, code);
        if (repository.findByCode(code).isPresent()) {
            throw new BusinessException("CONFLICT", resourceLabel + "已存在: " + code);
        }
    }

    /**
     * 持久化元数据定义。
     *
     * @param definition 元数据定义
     * @return 持久化后的元数据定义
     */
    protected T save(T definition) {
        log.info("保存{}定义，code={}", resourceLabel, definition.code());
        return repository.save(definition);
    }
}
