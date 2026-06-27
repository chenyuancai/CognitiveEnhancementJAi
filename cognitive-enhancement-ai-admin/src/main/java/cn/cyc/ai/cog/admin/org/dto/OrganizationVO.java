package cn.cyc.ai.cog.admin.org.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganizationVO {

    private Long id;
    private Long tenantId;
    private Long accountId;
    private String orgType;
    private String orgName;
    private String unifiedSocialCode;
    private Integer seatLimit;
    private String contactName;
    private String contactPhone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
