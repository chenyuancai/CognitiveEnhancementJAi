package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long accountId;

    @NotNull
    private Long packageId;

    /** SUBSCRIPTION / QUOTA */
    @NotNull
    private String orderType;
}
