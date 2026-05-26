package cn.cyc.ai.cog.core.metadata;

import java.util.List;
import java.util.Optional;

/**
 * 核心元数据最小服务接口。
 *
 * @param <T> 元数据类型
 */
public interface MetadataDefinitionService<T extends MetadataDefinition> {

    Optional<T> findByCode(String code);

    List<T> listAll();

    T save(T definition);
}
