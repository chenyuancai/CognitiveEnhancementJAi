package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色化工作台首页（2A）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class WorkbenchResult {

    /** 解析后的主角色：ADMIN / OPERATOR / CONTENT / SUPPORT */
    private String role;

    /** todos。 */
    private List<WorkbenchTodoItem> todos = new ArrayList<>();
    /** metrics。 */
    private List<WorkbenchMetricCard> metrics = new ArrayList<>();
    /** quickEntries。 */
    private List<WorkbenchQuickEntry> quickEntries = new ArrayList<>();
}
