package cn.cyc.ai.cog.admin.membership.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountMembershipVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private Long levelId;
    private String levelCode;
    private LocalDateTime expireAt;
    private String source;
}
