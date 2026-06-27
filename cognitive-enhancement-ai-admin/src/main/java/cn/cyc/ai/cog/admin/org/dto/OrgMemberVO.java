package cn.cyc.ai.cog.admin.org.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrgMemberVO {

    private Long id;
    private Long tenantId;
    private Long orgId;
    private Long userId;
    private Long deptId;
    private String orgRole;
    private String status;
    private LocalDateTime joinedAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
