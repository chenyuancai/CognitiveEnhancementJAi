package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通用内存元数据仓储实现，主要供配置类中的轻量 Bean 装配复用。
 *
 * @param <T> 元数据类型
 * @author cyc
 */
public abstract class InMemoryMetadataRepository<T extends MetadataDefinition> implements MetadataRepository<T> {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(InMemoryMetadataRepository.class);

    /**
     * 保持插入顺序的内存存储容器。
     */
    private final Map<String, T> storage = new LinkedHashMap<>();

    @Override
    public synchronized Optional<T> findByCode(String code) {
        log.debug("顺序内存仓储按编码查询定义，repository={}, code={}", getClass().getSimpleName(), code);
        return Optional.ofNullable(storage.get(code));
    }

    @Override
    public synchronized List<T> listAll() {
        log.debug("顺序内存仓储查询全部定义，repository={}, size={}", getClass().getSimpleName(), storage.size());
        return new ArrayList<>(storage.values());
    }

    @Override
    public synchronized T save(T definition) {
        log.info("顺序内存仓储保存定义，repository={}, code={}", getClass().getSimpleName(), definition.code());
        storage.put(definition.code(), definition);
        return definition;
    }
}
