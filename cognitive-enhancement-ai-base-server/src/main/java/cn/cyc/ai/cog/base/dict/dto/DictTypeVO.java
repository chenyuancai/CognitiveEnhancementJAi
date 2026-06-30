package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

/**
 * 字典类型视图。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DictTypeVO {

    /** 主键 ID */
    private Long id;

    /** 租户 ID */
    private Long tenantId;

    /** biz编码。 */
    private String bizCode;

    /** dictKind。 */
    private Integer dictKind;

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
}
