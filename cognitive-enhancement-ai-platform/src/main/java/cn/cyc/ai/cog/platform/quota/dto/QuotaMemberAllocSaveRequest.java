package cn.cyc.ai.cog.platform.quota.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuotaMemberAllocSaveRequest {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    @Min(1)
    private Long allocatedAmount;
}
