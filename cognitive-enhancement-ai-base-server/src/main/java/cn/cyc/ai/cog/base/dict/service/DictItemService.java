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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DictItemService {

    /** INTEGER值。 */
    private static final Pattern INTEGER_VALUE = Pattern.compile("^-?\\d+$");

    /** dictItem仓储。 */
    private final DictItemRepository dictItemRepository;

    /**
     * 创建DictItem服务。
     *
     * @param dictItemRepository dictItem仓储
     */
    public DictItemService(DictItemRepository dictItemRepository) {
        this.dictItemRepository = dictItemRepository;
    }

    /**
     * 执行save。
     *
     * @param request 请求
     * @param enumValue enum值
     * @return 执行结果
     */
    public DictItemVO save(DictItemSaveRequest request, boolean enumValue) {
        if (enumValue) {
            Errors.throwIf(!INTEGER_VALUE.matcher(request.getValue().trim()).matches(),
                    PlatformErrorCode.ENUM_VALUE_NOT_INTEGER);
        }
        return DictConverter.toItemVo(dictItemRepository.save(request));
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public boolean delete(Long id) {
        dictItemRepository.deleteById(id);
        return true;
    }

    /**
     * 查询Item列表。
     *
     * @param query 查询
     * @return 结果列表
     */
    public List<DictItemVO> list(DictItemListQuery query) {
        List<DictItemVO> flat = dictItemRepository.listByTypeId(query.getTypeId()).stream()
                .map(DictConverter::toItemVo)
                .toList();
        if (Boolean.TRUE.equals(query.getTreeFlag())) {
            return DictTreeBuilder.buildTree(flat, 0L);
        }
        return flat;
    }

    /**
     * 查询是否启用人编码列表。
     *
     * @param code 编码
     * @return 结果列表
     */
    public List<DictItemReadVO> listEnabledByCode(String code) {
        return dictItemRepository.listEnabledByTypeCode(code).stream()
                .map(DictConverter::toReadVo)
                .toList();
    }
}
