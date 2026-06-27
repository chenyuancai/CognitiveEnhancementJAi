package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionPackageSaveRequest {

    private Long id;

    @NotBlank
    private String packageCode;
    @NotBlank
    private String packageName;
    @NotBlank
    private String segment;
    @NotNull
    private Long levelId;
    @NotBlank
    private String billingPeriod;
    private Integer periodCount = 1;
    private Integer trialDays = 0;
    @NotNull
    private Long priceFen;
    private Long originalPriceFen;
    private Long cycleTokenQuota;
    private Integer seatCount = 1;
    private String saleMode = "SELF_SERVICE";
    private Boolean requireContract;
    private String status = "ON_SALE";
    private String snapshotJson;
}
