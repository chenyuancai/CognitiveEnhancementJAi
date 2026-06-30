package cn.cyc.ai.cog.platform.system.service;

import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.AuditLog;
import cn.cyc.ai.cog.platform.system.dto.AuditLogPageQuery;
import cn.cyc.ai.cog.platform.system.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * AuditLog服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AuditLogService {

    /** auditLog仓储。 */
    private final AuditLogRepository auditLogRepository;

    /**
     * 创建AuditLog服务。
     *
     * @param auditLogRepository auditLog仓储
     */
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<AuditLog> page(AuditLogPageQuery query) {
        return auditLogRepository.page(query);
    }

    /**
     * 执行record。
     */
    public void record(String action, String resourceType, String resourceId,
                       String beforeJson, String afterJson, String message) {
        AuditLog log = new AuditLog(
                null,
                TenantContext.currentTenantId(),
                UserContext.currentUserId(),
                UserContext.get() == null ? null : UserContext.get().getUsername(),
                action,
                message,
                resourceType,
                resourceId,
                beforeJson,
                afterJson,
                null,
                LocalDateTime.now()
        );
        auditLogRepository.append(log);
    }

    /** @deprecated 请使用带 message 的重载 */
    @Deprecated
    public void record(String action, String resourceType, String resourceId, String beforeJson, String afterJson) {
        record(action, resourceType, resourceId, beforeJson, afterJson, null);
    }
}
