package cn.cyc.ai.cog.base.dict.service;

import cn.cyc.ai.cog.base.dict.domain.DictItem;
import cn.cyc.ai.cog.base.dict.dto.DictItemListQuery;
import cn.cyc.ai.cog.base.dict.dto.DictItemReadVO;
import cn.cyc.ai.cog.base.dict.dto.DictItemSaveRequest;
import cn.cyc.ai.cog.base.dict.dto.DictItemVO;
import cn.cyc.ai.cog.base.dict.repository.DictItemRepository;
import cn.cyc.ai.cog.base.dict.support.DictConverter;
import cn.cyc.ai.cog.base.dict.support.DictTreeBuilder;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 字典项业务服务。
 */
@Service
public class DictItemService {

    private static final Pattern INTEGER_VALUE = Pattern.compile("^-?\\d+$");

    private final DictItemRepository dictItemRepository;

    public DictItemService(DictItemRepository dictItemRepository) {
        this.dictItemRepository = dictItemRepository;
    }

    public DictItemVO save(DictItemSaveRequest request, boolean enumValue) {
        if (enumValue) {
            Errors.throwIf(!INTEGER_VALUE.matcher(request.getValue().trim()).matches(),
                    PlatformErrorCode.ENUM_VALUE_NOT_INTEGER);
        }
        return DictConverter.toItemVo(dictItemRepository.save(request));
    }

    public boolean delete(Long id) {
        dictItemRepository.deleteById(id);
        return true;
    }

    public List<DictItemVO> list(DictItemListQuery query) {
        List<DictItemVO> flat = dictItemRepository.listByTypeId(query.getTypeId()).stream()
                .map(DictConverter::toItemVo)
                .toList();
        if (Boolean.TRUE.equals(query.getTreeFlag())) {
            return DictTreeBuilder.buildTree(flat, 0L);
        }
        return flat;
    }

    public List<DictItemReadVO> listEnabledByCode(String code) {
        return dictItemRepository.listEnabledByTypeCode(code).stream()
                .map(DictConverter::toReadVo)
                .toList();
    }
}
