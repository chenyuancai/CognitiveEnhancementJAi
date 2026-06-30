package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SubscriptionPackage实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_subscription_package")
public class SubscriptionPackageEntity extends BaseEntity {

    /** package编码。 */
    private String packageCode;
    /** package名称。 */
    private String packageName;
    /** segment。 */
    private String segment;
    /** 等级ID */
    private Long levelId;
    /** 计费Period。 */
    private String billingPeriod;
    /** period数量。 */
    private Integer periodCount;
    /** trialDays。 */
    private Integer trialDays;
    /** priceFen。 */
    private Long priceFen;
    /** originalPriceFen。 */
    private Long originalPriceFen;
    /** cycle令牌额度。 */
    private Long cycleTokenQuota;
    /** seat数量。 */
    private Integer seatCount;
    /** sale模式。 */
    private String saleMode;
    /** requireContract。 */
    private Boolean requireContract;
    /** 状态。 */
    private String status;
    /** 快照JSON。 */
    private String snapshotJson;
}
