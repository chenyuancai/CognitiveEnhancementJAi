package cn.cyc.ai.cog.platform.quota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuotaAdjustRequest {

    private Long accountId;

    @NotBlank
    private String bucket;

    @NotNull
    private Long deltaAmount;

    private String remark;
}
