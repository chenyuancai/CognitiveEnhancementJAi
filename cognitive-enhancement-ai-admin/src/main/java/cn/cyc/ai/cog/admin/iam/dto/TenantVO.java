package cn.cyc.ai.cog.admin.iam.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class TenantVO {

    /** 主键 ID */
    private Long id;
    /** 租户编码。 */
    private String tenantCode;
    /** 租户名称。 */
    private String tenantName;
    /** segment。 */
    private String segment;
    /** 状态。 */
    private String status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
