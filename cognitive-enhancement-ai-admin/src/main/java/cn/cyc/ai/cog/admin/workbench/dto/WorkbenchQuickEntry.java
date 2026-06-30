package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 角色化工作台快捷入口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class WorkbenchQuickEntry {

    /** 键。 */
    private String key;
    /** label。 */
    private String label;
    /** link。 */
    private String link;
    /** 权限。 */
    private String permission;
}
