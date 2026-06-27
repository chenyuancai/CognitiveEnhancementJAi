package cn.cyc.ai.cog.platform.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GrantMembershipRequest {

    @NotNull
    private Long accountId;

    @NotNull
    private Long levelId;

    private LocalDateTime expireAt;
    private String remark;
}
