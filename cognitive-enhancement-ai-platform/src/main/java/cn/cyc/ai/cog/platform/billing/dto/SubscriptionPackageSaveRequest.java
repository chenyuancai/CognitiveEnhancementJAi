package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * SubscriptionPackageSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SubscriptionPackageSaveRequest {

    /** 主键 ID */
    private Long id;

    /** package编码。 */
    @NotBlank
    private String packageCode;
    /** package名称。 */
    @NotBlank
    private String packageName;
    /** segment。 */
    @NotBlank
    private String segment;
    /** 等级ID */
    @NotNull
    private Long levelId;
    /** 计费Period。 */
    @NotBlank
    private String billingPeriod;
    /** period数量。 */
    private Integer periodCount = 1;
    /** trialDays。 */
    private Integer trialDays = 0;
    /** priceFen。 */
    @NotNull
    private Long priceFen;
    /** originalPriceFen。 */
    private Long originalPriceFen;
    /** cycle令牌额度。 */
    private Long cycleTokenQuota;
    /** seat数量。 */
    private Integer seatCount = 1;
    /** sale模式。 */
    private String saleMode = "SELF_SERVICE";
    /** requireContract。 */
    private Boolean requireContract;
    /** 状态。 */
    private String status = "ON_SALE";
    /** 快照JSON。 */
    private String snapshotJson;
}
