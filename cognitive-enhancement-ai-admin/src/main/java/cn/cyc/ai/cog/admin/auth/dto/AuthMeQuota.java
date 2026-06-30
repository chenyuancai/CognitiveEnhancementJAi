package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

/**
 * AuthMeQuota
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeQuota {
    /** cycleRemaining。 */
    private Long cycleRemaining;
    /** giftRemaining。 */
    private Long giftRemaining;
    /** topupRemaining。 */
    private Long topupRemaining;
}
