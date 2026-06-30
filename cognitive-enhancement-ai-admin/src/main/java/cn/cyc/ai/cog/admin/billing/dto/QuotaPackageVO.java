package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

/**
 * 额度Package视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class QuotaPackageVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** package编码。 */
    private String packageCode;
    /** package名称。 */
    private String packageName;
    /** segment。 */
    private String segment;
    /** 令牌Amount。 */
    private Long tokenAmount;
    /** priceFen。 */
    private Long priceFen;
    /** validDays。 */
    private Integer validDays;
    /** 状态。 */
    private String status;
}
