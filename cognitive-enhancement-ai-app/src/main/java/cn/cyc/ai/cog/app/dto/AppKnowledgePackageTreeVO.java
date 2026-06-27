package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * C 端知识包目录树节点。
 */
@Data
public class AppKnowledgePackageTreeVO {

    private Long id;
    private String packageName;
    private String description;
    private List<AppKnowledgeTreeItemVO> items = new ArrayList<>();
}
