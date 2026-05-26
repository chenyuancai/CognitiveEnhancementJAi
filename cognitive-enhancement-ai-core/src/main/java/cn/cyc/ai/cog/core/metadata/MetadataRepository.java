package cn.cyc.ai.cog.core.metadata;

import java.util.List;
import java.util.Optional;

/**
 * 核心元数据最小仓储接口。
 *
 * @param <T> 元数据类型
 * @author cyc
 */
public interface MetadataRepository<T extends MetadataDefinition> {

    /**
     * 按编码查询单条元数据定义。
     *
     * @param code 元数据编码
     * @return 元数据定义
     */
    Optional<T> findByCode(String code);

    /**
     * 查询全部元数据定义。
     *
     * @return 元数据定义列表
     */
    List<T> listAll();

    /**
     * 保存元数据定义。
     *
     * @param definition 元数据定义
     * @return 保存后的元数据定义
     */
    T save(T definition);
}
