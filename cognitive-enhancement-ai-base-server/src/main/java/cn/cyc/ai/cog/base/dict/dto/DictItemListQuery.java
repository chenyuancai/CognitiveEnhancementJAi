package cn.cyc.ai.cog.base.dict.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典项列表查询。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictItemListQuery {

    /** 类型ID */
    @NotNull(message = "字典类型 id 不能为空")
    private Long typeId;

    /** 是否返回树形结构 */
    private Boolean treeFlag = Boolean.FALSE;
}
