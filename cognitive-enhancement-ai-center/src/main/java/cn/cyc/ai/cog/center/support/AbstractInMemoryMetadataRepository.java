package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 通用内存元数据仓储基类，提供线程安全的主链路元数据读写能力。
 *
 * @param <T> 元数据定义类型
 * @author cyc
 */
public abstract class AbstractInMemoryMetadataRepository<T extends MetadataDefinition> implements MetadataRepository<T> {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractInMemoryMetadataRepository.class);

    /**
     * 基于编码索引的内存存储容器。
     */
    private final ConcurrentMap<String, T> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<T> findByCode(String code) {
        log.debug("内存仓储按编码查询定义，repository={}, code={}", getClass().getSimpleName(), code);
        return Optional.ofNullable(storage.get(code));
    }

    @Override
    public List<T> listAll() {
        log.debug("内存仓储查询全部定义，repository={}, size={}", getClass().getSimpleName(), storage.size());
        return storage.values().stream()
                .sorted((left, right) -> left.code().compareTo(right.code()))
                .toList();
    }

    @Override
    public T save(T definition) {
        log.info("内存仓储保存定义，repository={}, code={}", getClass().getSimpleName(), definition.code());
        storage.put(definition.code(), definition);
        return definition;
    }
}
