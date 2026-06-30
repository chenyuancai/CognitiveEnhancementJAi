package cn.cyc.ai.cog.platform.org.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DepartmentSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class DepartmentSaveRequest {

    /** orgID */
    private Long orgId;

    /** 主键 ID */
    private Long id;

    /** parentID */
    private Long parentId = 0L;

    /** dept名称。 */
    @NotBlank
    private String deptName;

    /** sortNo。 */
    private Integer sortNo = 0;
}
