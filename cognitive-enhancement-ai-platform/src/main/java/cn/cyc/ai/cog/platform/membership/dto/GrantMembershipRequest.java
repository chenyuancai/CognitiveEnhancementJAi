package cn.cyc.ai.cog.platform.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Grant会员请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class GrantMembershipRequest {

    /** 账户ID */
    @NotNull
    private Long accountId;

    /** 等级ID */
    @NotNull
    private Long levelId;

    /** expireAt。 */
    private LocalDateTime expireAt;
    /** remark。 */
    private String remark;
}
