package cn.cyc.ai.cog.platform.org.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentSaveRequest {

    private Long orgId;

    private Long id;

    private Long parentId = 0L;

    @NotBlank
    private String deptName;

    private Integer sortNo = 0;
}
