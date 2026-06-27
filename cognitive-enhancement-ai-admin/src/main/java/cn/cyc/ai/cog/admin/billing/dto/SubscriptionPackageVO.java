package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

@Data
public class SubscriptionPackageVO {

    private Long id;
    private Long tenantId;
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
