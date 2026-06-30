package cn.cyc.ai.cog.core.metadata;

import java.util.List;
import java.util.Optional;

/**
 * 核心元数据最小服务接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface MetadataDefinitionService<T extends MetadataDefinition> {

    Optional<T> findByCode(String code);

    List<T> listAll();

    T save(T definition);
}
