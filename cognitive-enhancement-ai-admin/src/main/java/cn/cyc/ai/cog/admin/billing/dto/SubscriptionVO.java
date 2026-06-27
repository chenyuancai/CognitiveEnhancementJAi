package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long orderId;
    private Long packageId;
    private String levelCode;
    private String status;
    private String phase;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean autoRenew;
    private String packageSnapshotJson;
}
