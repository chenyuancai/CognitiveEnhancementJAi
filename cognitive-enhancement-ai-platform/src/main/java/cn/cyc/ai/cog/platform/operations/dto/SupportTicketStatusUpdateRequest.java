package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支持Ticket状态更新请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SupportTicketStatusUpdateRequest {

    /** 主键 ID */
    private Long id;

    /** 状态。 */
    @NotBlank
    private String status;
    /** assignee用户ID */
    private Long assigneeUserId;
}
