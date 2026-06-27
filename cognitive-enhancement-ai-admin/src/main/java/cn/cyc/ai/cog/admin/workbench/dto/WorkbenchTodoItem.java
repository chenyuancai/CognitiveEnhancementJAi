package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 角色化工作台待办项。
 */
@Data
public class WorkbenchTodoItem {

    private String key;
    private String label;
    private long count;
    private String link;
}
