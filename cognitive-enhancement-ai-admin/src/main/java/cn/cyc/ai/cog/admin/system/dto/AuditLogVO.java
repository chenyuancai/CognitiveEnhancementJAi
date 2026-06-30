package cn.cyc.ai.cog.admin.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AuditLog视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuditLogVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** operatorID */
    private Long operatorId;
    /** operator名称。 */
    private String operatorName;
    /** action。 */
    private String action;
    /** 消息。 */
    private String message;
    /** resource类型。 */
    private String resourceType;
    /** resourceID */
    private String resourceId;
    /** beforeJSON。 */
    private String beforeJson;
    /** afterJSON。 */
    private String afterJson;
    /** ipAddress。 */
    private String ipAddress;
    /** 创建时间 */
    private LocalDateTime createTime;
}
