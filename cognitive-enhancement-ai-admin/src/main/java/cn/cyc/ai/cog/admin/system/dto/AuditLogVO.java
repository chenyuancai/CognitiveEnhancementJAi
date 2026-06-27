package cn.cyc.ai.cog.admin.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogVO {

    private Long id;
    private Long tenantId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String message;
    private String resourceType;
    private String resourceId;
    private String beforeJson;
    private String afterJson;
    private String ipAddress;
    private LocalDateTime createTime;
}
