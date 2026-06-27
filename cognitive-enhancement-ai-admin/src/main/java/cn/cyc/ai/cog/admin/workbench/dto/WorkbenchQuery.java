package cn.cyc.ai.cog.admin.workbench.dto;

import lombok.Data;

/**
 * 工作台查询参数。
 */
@Data
public class WorkbenchQuery {

    /** 起始日期（ISO-8601），缺省取近 7 日。 */
    private String from;

    /** 结束日期（ISO-8601），缺省取今日。 */
    private String to;

    /** 为 true 时绕过 60s 缓存强制重算。 */
    private Boolean refresh;
}
