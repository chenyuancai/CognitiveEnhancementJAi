package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

@Data
public class AuthMeQuota {
    private Long cycleRemaining;
    private Long giftRemaining;
    private Long topupRemaining;
}
