package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuotaPackageSaveRequest {

    private Long id;

    @NotBlank
    private String packageCode;
    @NotBlank
    private String packageName;
    @NotBlank
    private String segment;
    @NotNull
    private Long tokenAmount;
    @NotNull
    private Long priceFen;
    private Integer validDays = 0;
    private String status = "ON_SALE";
}
