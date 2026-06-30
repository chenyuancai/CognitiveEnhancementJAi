package cn.cyc.ai.cog.platform.iam.dto;

import lombok.Data;

/**
 * 用户状态更新请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class UserStatusUpdateRequest {

    /** 主键 ID */
    private Long id;

    /** 状态。 */
    private String status;
    /** ban原因。 */
    private String banReason;
    /** banUntil。 */
    private java.time.LocalDateTime banUntil;
}
