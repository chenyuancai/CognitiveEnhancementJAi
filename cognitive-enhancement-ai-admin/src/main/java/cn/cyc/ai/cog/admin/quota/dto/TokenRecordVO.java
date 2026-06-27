package cn.cyc.ai.cog.admin.quota.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenRecordVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long memberUserId;
    private String recordType;
    private String bucket;
    private Long deltaAmount;
    private Long balanceAfter;
    private String bizType;
    private String bizId;
    private String idempotencyKey;
    private String message;
    private LocalDateTime createTime;
}
