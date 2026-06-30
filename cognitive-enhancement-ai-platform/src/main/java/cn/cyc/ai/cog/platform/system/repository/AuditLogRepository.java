package cn.cyc.ai.cog.platform.system.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.AuditLog;
import cn.cyc.ai.cog.platform.system.dto.AuditLogPageQuery;

/**
 * AuditLog仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface AuditLogRepository {

    PageResult<AuditLog> page(AuditLogPageQuery query);

    void append(AuditLog log);
}
