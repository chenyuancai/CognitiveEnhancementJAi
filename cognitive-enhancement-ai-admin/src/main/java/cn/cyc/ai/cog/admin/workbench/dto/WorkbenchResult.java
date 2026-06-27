package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色化工作台首页（2A）。
 */
@Data
public class WorkbenchResult {

    /** 解析后的主角色：ADMIN / OPERATOR / CONTENT / SUPPORT */
    private String role;

    private List<WorkbenchTodoItem> todos = new ArrayList<>();
    private List<WorkbenchMetricCard> metrics = new ArrayList<>();
    private List<WorkbenchQuickEntry> quickEntries = new ArrayList<>();
}
