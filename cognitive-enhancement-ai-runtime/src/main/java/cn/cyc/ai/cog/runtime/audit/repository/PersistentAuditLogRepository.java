package cn.cyc.ai.cog.runtime.audit.repository;

import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.entity.AuditLogEntity;
import cn.cyc.ai.cog.runtime.audit.mapper.AuditLogMapper;
import cn.cyc.ai.cog.runtime.audit.spi.AuditLogRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 基于 MyBatis Plus 的审计日志仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentAuditLogRepository implements AuditLogRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentAuditLogRepository.class);

    /**
     * 审计日志 Mapper。
     */
    private final AuditLogMapper auditLogMapper;

    /**
     * JSON 序列化器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造审计日志持久化仓储。
     *
     * @param auditLogMapper 审计日志 Mapper
     * @param objectMapper   JSON 序列化器
     */
    public PersistentAuditLogRepository(AuditLogMapper auditLogMapper, ObjectMapper objectMapper) {
        this.auditLogMapper = auditLogMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 保存审计日志。
     *
     * @param record 审计日志
     */
    @Override
    public void save(AuditLogRecord record) {
        auditLogMapper.insert(toEntity(record));
        log.debug("持久化审计日志, eventType={}, action={}, resourceCode={}",
                record.eventType(), record.action(), record.resourceCode());
    }

    /**
     * 查询当前租户全部审计日志。
     *
     * @return 审计日志列表
     */
    @Override
    public List<AuditLogRecord> listAll() {
        LambdaQueryWrapper<AuditLogEntity> queryWrapper = new LambdaQueryWrapper<AuditLogEntity>()
                .eq(AuditLogEntity::getTenantId, TenantContext.currentTenantId())
                .orderByDesc(AuditLogEntity::getRecordedAt)
                .orderByDesc(AuditLogEntity::getId);
        return auditLogMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    private AuditLogEntity toEntity(AuditLogRecord record) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setTenantId(TenantIds.resolveId(record.tenantCode()));
        entity.setTraceId(record.traceId());
        entity.setEventType(record.eventType());
        entity.setAction(record.action());
        entity.setResourceType(record.resourceType());
        entity.setResourceCode(record.resourceCode());
        entity.setOperator(record.operator());
        entity.setSuccess(record.success());
        entity.setDetailJson(toJson(record.detail()));
        entity.setRecordedAt(record.recordedAt());
        return entity;
    }

    private AuditLogRecord toDomain(AuditLogEntity entity) {
        return new AuditLogRecord(
                TenantIds.toCode(entity.getTenantId()),
                entity.getTraceId(),
                entity.getEventType(),
                entity.getAction(),
                entity.getResourceType(),
                entity.getResourceCode(),
                entity.getOperator(),
                Boolean.TRUE.equals(entity.getSuccess()),
                parseDetail(entity.getDetailJson()),
                entity.getRecordedAt()
        );
    }

    private String toJson(Map<String, Object> detail) {
        if (detail == null || detail.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            log.warn("审计日志详情 JSON 序列化失败", e);
            return "{}";
        }
    }

    private Map<String, Object> parseDetail(String detailJson) {
        if (detailJson == null || detailJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(detailJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("审计日志详情 JSON 反序列化失败", e);
            return Map.of();
        }
    }
}
