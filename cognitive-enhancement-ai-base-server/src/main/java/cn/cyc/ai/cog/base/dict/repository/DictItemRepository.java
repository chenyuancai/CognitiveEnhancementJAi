package cn.cyc.ai.cog.base.dict.repository;

import cn.cyc.ai.cog.base.dict.domain.DictItem;
import cn.cyc.ai.cog.base.dict.dto.DictItemSaveRequest;

import java.util.List;
import java.util.Optional;

/**
 * 字典项仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface DictItemRepository {

    DictItem save(DictItemSaveRequest request);

    void deleteById(Long id);

    void deleteByTypeId(Long typeId);

    Optional<DictItem> findById(Long id);

    List<DictItem> listByTypeId(Long typeId);

    List<DictItem> listEnabledByTypeCode(String code);
}
