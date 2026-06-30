package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 额度PackageSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class QuotaPackageSaveRequest {

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
    /** 令牌Amount。 */
    @NotNull
    private Long tokenAmount;
    /** priceFen。 */
    @NotNull
    private Long priceFen;
    /** validDays。 */
    private Integer validDays = 0;
    /** 状态。 */
    private String status = "ON_SALE";
}
