package cn.cyc.ai.cog.platform.operations.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupportTicketPageQuery extends PageQuery {

    private String keyword;
    private String status;
    private String category;
    private String priority;
    private Long assigneeUserId;
    private Long submitterUserId;
}
