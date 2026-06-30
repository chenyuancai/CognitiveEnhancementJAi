package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 知识PackageSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class KnowledgePackageSaveRequest {

    /** 主键 ID */
    private Long id;

    /** package名称。 */
    @NotBlank
    private String packageName;
    /** 描述。 */
    private String description;
    /** 状态。 */
    private String status;
}
