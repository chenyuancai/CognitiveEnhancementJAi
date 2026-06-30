package cn.cyc.ai.cog.runtime.audit.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 审计日志实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_audit_log")
public class AuditLogEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 链路追踪 ID。
     */
    private String traceId;

    /**
     * 事件类型。
     */
    private String eventType;

    /**
     * 操作动作。
     */
    private String action;

    /**
     * 资源类型。
     */
    private String resourceType;

    /**
     * 资源编码。
     */
    private String resourceCode;

    /**
     * 操作人。
     */
    private String operator;

    /**
     * 是否成功。
     */
    private Boolean success;

    /**
     * 审计详情 JSON。
     */
    private String detailJson;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
