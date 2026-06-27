package cn.cyc.ai.cog.admin.org.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrgDepartmentVO {

    private Long id;
    private Long tenantId;
    private Long orgId;
    private Long parentId;
    private String deptName;
    private Integer sortNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
