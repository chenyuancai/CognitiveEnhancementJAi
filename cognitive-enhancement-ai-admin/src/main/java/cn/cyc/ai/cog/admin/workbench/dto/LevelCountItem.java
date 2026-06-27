package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 会员等级分布项。
 */
@Data
public class LevelCountItem {

    private String level;
    private long count;
}
