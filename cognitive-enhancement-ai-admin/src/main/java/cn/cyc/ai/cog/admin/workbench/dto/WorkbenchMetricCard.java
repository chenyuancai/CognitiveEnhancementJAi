package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 角色化工作台指标卡片。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class WorkbenchMetricCard {

    /** 键。 */
    private String key;
    /** label。 */
    private String label;
    /** 值。 */
    private long value;
    /** unit。 */
    private String unit;
    /** trend。 */
    private String trend;
}
