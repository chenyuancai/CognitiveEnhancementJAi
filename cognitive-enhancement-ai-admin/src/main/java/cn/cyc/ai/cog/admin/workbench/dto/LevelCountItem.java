package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 会员等级分布项。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class LevelCountItem {

    /** 等级。 */
    private String level;
    /** 数量。 */
    private long count;
}
