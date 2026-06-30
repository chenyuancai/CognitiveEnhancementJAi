package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识包目录树条目。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppKnowledgeTreeItemVO {

    /** 主键 ID */
    private Long id;
    /** 标题。 */
    private String title;
    /** 内容ID */
    private Long contentId;
    /** locked。 */
    private boolean locked;
    /** children。 */
    private List<AppKnowledgeTreeItemVO> children = new ArrayList<>();
}
