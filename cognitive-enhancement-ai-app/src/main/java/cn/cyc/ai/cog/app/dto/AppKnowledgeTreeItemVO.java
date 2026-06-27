package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识包目录树条目。
 */
@Data
public class AppKnowledgeTreeItemVO {

    private Long id;
    private String title;
    private Long contentId;
    private boolean locked;
    private List<AppKnowledgeTreeItemVO> children = new ArrayList<>();
}
