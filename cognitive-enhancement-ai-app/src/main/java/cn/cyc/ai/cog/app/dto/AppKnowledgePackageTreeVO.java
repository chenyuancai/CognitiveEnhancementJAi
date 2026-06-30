package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * C 端知识包目录树节点。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppKnowledgePackageTreeVO {

    /** 主键 ID */
    private Long id;
    /** package名称。 */
    private String packageName;
    /** 描述。 */
    private String description;
    /** items。 */
    private List<AppKnowledgeTreeItemVO> items = new ArrayList<>();
}
