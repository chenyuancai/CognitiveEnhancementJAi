package cn.cyc.ai.cog.platform.org.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgMemberSaveRequest {

    private Long id;

    private Long orgId;

    @NotNull
    private Long userId;

    private Long deptId;
    private String orgRole = "MEMBER";
}
