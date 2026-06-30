package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * FinancialRecord视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class FinancialRecordVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** 订单ID */
    private Long orderId;
    /** record类型。 */
    private String recordType;
    /** 消息。 */
    private String message;
    /** amountFen。 */
    private Long amountFen;
    /** balanceAfterFen。 */
    private Long balanceAfterFen;
    /** remark。 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
}
