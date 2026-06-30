package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class CreateOrderRequest {

    /** 账户ID */
    @NotNull
    private Long accountId;

    /** packageID */
    @NotNull
    private Long packageId;

    /** SUBSCRIPTION / QUOTA */
    @NotNull
    private String orderType;
}
