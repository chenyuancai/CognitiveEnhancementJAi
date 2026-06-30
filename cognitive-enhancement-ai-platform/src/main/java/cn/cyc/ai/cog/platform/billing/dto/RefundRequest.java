package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 手动退款请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class RefundRequest {

    /** 订单ID */
    private Long orderId;

    /** 退款金额（分），不得超过订单金额。 */
    @NotNull(message = "退款金额不能为空")
    @Positive(message = "退款金额必须大于0")
    private Long refundAmount;

    /** 退款原因。 */
    private String remark;
}
