package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderVO {

    private Long id;
    private Long tenantId;
    private String orderNo;
    private Long accountId;
    private Long buyerUserId;
    private String orderType;
    private Long packageId;
    private String packageSnapshotJson;
    private Long amountFen;
    private String currency;
    private String status;
    private String payChannel;
    private LocalDateTime payTime;
    private LocalDateTime fulfillTime;
    private String idempotencyKey;
    private Long refundAmountFen;
    private LocalDateTime refundTime;
    private String remark;
}
