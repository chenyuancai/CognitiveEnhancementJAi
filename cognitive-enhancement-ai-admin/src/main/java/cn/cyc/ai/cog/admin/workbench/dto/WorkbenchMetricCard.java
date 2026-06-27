package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 角色化工作台指标卡片。
 */
@Data
public class WorkbenchMetricCard {

    private String key;
    private String label;
    private long value;
    private String unit;
    private String trend;
}
