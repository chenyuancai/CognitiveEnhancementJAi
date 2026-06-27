package cn.cyc.ai.cog.runtime.audit.spi;

import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;

import java.util.List;

/**
 * 审计日志仓储接口。
 *
 * @author cyc
 */
public interface AuditLogRepository {

    /**
     * 保存审计日志。
     *
     * @param record 审计日志
     */
    void save(AuditLogRecord record);

    /**
     * 查询当前租户全部审计日志。
     *
     * @return 审计日志列表
     */
    List<AuditLogRecord> listAll();
}
