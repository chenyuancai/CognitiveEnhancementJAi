package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 角色化工作台待办项。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class WorkbenchTodoItem {

    /** 键。 */
    private String key;
    /** label。 */
    private String label;
    /** 数量。 */
    private long count;
    /** link。 */
    private String link;
}
