package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 角色化工作台快捷入口。
 */
@Data
public class WorkbenchQuickEntry {

    private String key;
    private String label;
    private String link;
    private String permission;
}
