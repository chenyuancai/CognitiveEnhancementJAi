package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinancialRecordVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long orderId;
    private String recordType;
    private String message;
    private Long amountFen;
    private Long balanceAfterFen;
    private String remark;
    private LocalDateTime createTime;
}
