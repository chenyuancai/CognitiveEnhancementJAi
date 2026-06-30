package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典项列表视图（支持树形 children）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictItemVO {

    /** 主键 ID */
    private Long id;

    /** biz编码。 */
    private String bizCode;

    /** 类型ID */
    private Long typeId;

    /** parentID */
    private Long parentId;

    /** 值。 */
    private String value;

    /** label。 */
    private String label;

    /** enLabel。 */
    private String enLabel;

    /** remark。 */
    private String remark;

    /** sort。 */
    private Integer sort;

    /** 状态。 */
    private Boolean status;

    /** children。 */
    private List<DictItemVO> children = new ArrayList<>();
}
