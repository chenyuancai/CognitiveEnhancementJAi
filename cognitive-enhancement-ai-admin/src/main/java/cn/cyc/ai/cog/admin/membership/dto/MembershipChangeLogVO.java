package cn.cyc.ai.cog.admin.membership.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MembershipChangeLogVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private String fromLevelCode;
    private String toLevelCode;
    private String changeType;
    private Long operatorId;
    private String message;
    private String remark;
    private LocalDateTime createTime;
}
