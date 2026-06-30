package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支持TicketSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SupportTicketSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 标题。 */
    @NotBlank
    private String title;
    /** body。 */
    private String body;
    /** category。 */
    private String category;
    /** priority。 */
    private String priority;
    /** submitter用户ID */
    private Long submitterUserId;
    /** assignee用户ID */
    private Long assigneeUserId;
}
