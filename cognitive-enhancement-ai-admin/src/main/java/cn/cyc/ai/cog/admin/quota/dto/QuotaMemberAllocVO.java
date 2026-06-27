package cn.cyc.ai.cog.admin.quota.dto;

import lombok.Data;

@Data
public class QuotaMemberAllocVO {

    private Long id;
    private Long accountId;
    private Long userId;
    private Long allocatedAmount;
    private Long usedAmount;
}
