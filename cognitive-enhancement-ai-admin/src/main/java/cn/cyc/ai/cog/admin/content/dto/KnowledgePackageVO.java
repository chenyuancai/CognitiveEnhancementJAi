package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

/**
 * 知识Package视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class KnowledgePackageVO {

    /** 主键 ID */
    private Long id;
    /** package名称。 */
    private String packageName;
    /** 描述。 */
    private String description;
    /** 状态。 */
    private String status;
}
