package cn.cyc.ai.cog.platform.membership.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 会员等级Save请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MembershipLevelSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 等级编码。 */
    @NotBlank
    private String levelCode;

    /** 等级名称。 */
    @NotBlank
    private String levelName;

    /** segment。 */
    @NotBlank
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
