package cn.cyc.ai.cog.admin.iam.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantVO {

    private Long id;
    private String tenantCode;
    private String tenantName;
    private String segment;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
