package cn.cyc.ai.cog.platform.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AuditLog实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_sys_audit_log")
public class AuditLogEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
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
