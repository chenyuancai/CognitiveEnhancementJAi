package cn.cyc.ai.cog.base.dict.service;

import cn.cyc.ai.cog.base.config.BaseServiceProperties;
import cn.cyc.ai.cog.base.dict.domain.DictType;
import cn.cyc.ai.cog.base.dict.dto.DictTypePageQuery;
import cn.cyc.ai.cog.base.dict.dto.DictTypeSaveRequest;
import cn.cyc.ai.cog.base.dict.dto.DictTypeTreeVO;
import cn.cyc.ai.cog.base.dict.dto.DictTypeVO;
import cn.cyc.ai.cog.base.dict.dto.DictItemVO;
import cn.cyc.ai.cog.base.dict.repository.DictItemRepository;
import cn.cyc.ai.cog.base.dict.repository.DictTypeRepository;
import cn.cyc.ai.cog.base.dict.support.DictConverter;
import cn.cyc.ai.cog.base.dict.support.DictTreeBuilder;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.page.PageResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型业务服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DictTypeService {

    /** dict类型仓储。 */
    private final DictTypeRepository dictTypeRepository;
    /** dictItem仓储。 */
    private final DictItemRepository dictItemRepository;
    /** base服务Properties。 */
    private final BaseServiceProperties baseServiceProperties;

    /**
     * 创建Dict类型服务。
     */
    public DictTypeService(DictTypeRepository dictTypeRepository,
                           DictItemRepository dictItemRepository,
                           BaseServiceProperties baseServiceProperties) {
        this.dictTypeRepository = dictTypeRepository;
        this.dictItemRepository = dictItemRepository;
        this.baseServiceProperties = baseServiceProperties;
    }

    /**
     * 执行save。
     *
     * @param dictKind dictKind
     * @param request 请求
     * @return 执行结果
     */
    public DictTypeVO save(int dictKind, DictTypeSaveRequest request) {
        return DictConverter.toTypeVo(dictTypeRepository.save(dictKind, request));
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public boolean delete(Long id) {
        dictTypeRepository.deleteById(id);
        return true;
    }

    /**
     * 执行分页。
     *
     * @param dictKind dictKind
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<DictTypeVO> page(int dictKind, DictTypePageQuery query) {
        PageResult<DictType> page = dictTypeRepository.page(dictKind, query);
        List<DictTypeVO> records = page.getRecords().stream().map(DictConverter::toTypeVo).toList();
        return PageResult.of(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 获取人编码。
     *
     * @param dictKind dictKind
     * @param code 编码
     * @return 人编码
     */
    public DictTypeVO getByCode(int dictKind, String code) {
        DictType type = dictTypeRepository.findByCode(
                dictKind,
                baseServiceProperties.getDefaultBizCode(),
                baseServiceProperties.getDefaultShareScope(),
                1L,
                code
        ).orElseThrow(() -> Errors.of(PlatformErrorCode.DICT_TYPE_NOT_FOUND, "字典类型不存在：" + code));
        return DictConverter.toTypeVo(type);
    }

    /**
     * 执行tree。
     *
     * @param dictKind dictKind
     * @param code 编码
     * @return 执行结果
     */
    public List<DictTypeTreeVO> tree(int dictKind, String code) {
        List<DictType> types = dictTypeRepository.listByCode(dictKind, code);
        List<DictTypeTreeVO> result = new ArrayList<>();
        for (DictType type : types) {
            DictTypeTreeVO tree = DictConverter.toTypeTreeVo(type);
            List<DictItemVO> items = dictItemRepository.listByTypeId(type.id()).stream()
                    .map(DictConverter::toItemVo)
                    .toList();
            tree.setDetailList(DictTreeBuilder.buildTree(items, 0L));
            result.add(tree);
        }
        return result;
    }
}
