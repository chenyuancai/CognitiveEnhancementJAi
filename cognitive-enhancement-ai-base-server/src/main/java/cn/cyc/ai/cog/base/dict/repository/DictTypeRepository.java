package cn.cyc.ai.cog.base.dict.repository;

import cn.cyc.ai.cog.base.dict.domain.DictType;
import cn.cyc.ai.cog.base.dict.dto.DictTypePageQuery;
import cn.cyc.ai.cog.base.dict.dto.DictTypeSaveRequest;
import cn.cyc.ai.cog.common.page.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * 字典类型仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface DictTypeRepository {

    DictType save(int dictKind, DictTypeSaveRequest request);

    void deleteById(Long id);

    Optional<DictType> findById(Long id);

    Optional<DictType> findByCode(int dictKind, String bizCode, String shareScope, Long tenantId, String code);

    PageResult<DictType> page(int dictKind, DictTypePageQuery query);

    List<DictType> listByCode(int dictKind, String code);
}
