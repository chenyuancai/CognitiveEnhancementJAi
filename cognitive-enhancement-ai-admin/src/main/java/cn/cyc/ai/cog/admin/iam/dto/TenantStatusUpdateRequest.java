package cn.cyc.ai.cog.admin.iam.dto;

import lombok.Data;

/**
 * 租户状态更新请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class TenantStatusUpdateRequest {

    /** 主键 ID */
    private Long id;

    /** 状态。 */
    private String status;
}
