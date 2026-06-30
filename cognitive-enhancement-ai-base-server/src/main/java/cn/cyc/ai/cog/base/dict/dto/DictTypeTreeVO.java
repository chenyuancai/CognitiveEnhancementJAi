package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型树视图（类型 + 项树）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictTypeTreeVO {

    /** 主键 ID */
    private Long id;

    /** biz编码。 */
    private String bizCode;

    /** shareScope。 */
    private String shareScope;

    /** 编码。 */
    private String code;

    /** 名称。 */
    private String name;

    /** en名称。 */
    private String enName;

    /** 描述。 */
    private String description;

    /** remark。 */
    private String remark;

    /** 状态。 */
    private Boolean status;

    /** detail列表。 */
    private List<DictItemVO> detailList = new ArrayList<>();
}
