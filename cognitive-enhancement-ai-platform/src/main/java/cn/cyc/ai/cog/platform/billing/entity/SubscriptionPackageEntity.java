package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_subscription_package")
public class SubscriptionPackageEntity extends BaseEntity {

    private String packageCode;
    private String packageName;
    private String segment;
    private Long levelId;
    private String billingPeriod;
    private Integer periodCount;
    private Integer trialDays;
    private Long priceFen;
    private Long originalPriceFen;
    private Long cycleTokenQuota;
    private Integer seatCount;
    private String saleMode;
    private Boolean requireContract;
    private String status;
    private String snapshotJson;
}
