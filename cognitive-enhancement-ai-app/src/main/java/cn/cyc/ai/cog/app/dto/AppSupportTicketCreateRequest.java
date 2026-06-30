package cn.cyc.ai.cog.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * C端支持Ticket创建请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppSupportTicketCreateRequest {

    /** 标题。 */
    @NotBlank
    private String title;
    /** body。 */
    private String body;
    /** category。 */
    private String category;
    /** priority。 */
    private String priority;
}
