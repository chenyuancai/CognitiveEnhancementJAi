package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 额度Package实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_quota_package")
public class QuotaPackageEntity extends BaseEntity {

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
