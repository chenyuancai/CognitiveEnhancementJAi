package cn.cyc.ai.cog.admin.quota.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuotaAccountVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long cycleRemaining;
    private Long cycleTotal;
    private LocalDateTime cycleResetAt;
    private Long giftRemaining;
    private Long giftTotal;
    private Long topupRemaining;
    private Long topupTotal;
}
