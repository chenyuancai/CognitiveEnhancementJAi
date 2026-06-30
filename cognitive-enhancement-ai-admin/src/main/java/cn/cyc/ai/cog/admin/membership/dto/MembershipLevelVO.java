package cn.cyc.ai.cog.admin.membership.dto;

import lombok.Data;

/**
 * 会员等级视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MembershipLevelVO {

    /** 主键 ID */
    private Long id;
    /** 等级编码。 */
    private String levelCode;
    /** 等级名称。 */
    private String levelName;
    /** segment。 */
    private String segment;
    /** is默认。 */
    private Boolean isDefault;
    /** sortNo。 */
    private Integer sortNo;
    /** 状态。 */
    private String status;
    /** benefitsJSON。 */
    private String benefitsJson;
}
