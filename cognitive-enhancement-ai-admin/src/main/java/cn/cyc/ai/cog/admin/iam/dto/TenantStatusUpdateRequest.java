package cn.cyc.ai.cog.admin.iam.dto;

import lombok.Data;

/**
 * 租户状态更新请求。
 */
@Data
public class TenantStatusUpdateRequest {

    private Long id;

    private String status;
}
