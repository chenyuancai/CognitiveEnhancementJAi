package cn.cyc.ai.cog.runtime.observation.repository;

import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.entity.ExecutionRecordEntity;
import cn.cyc.ai.cog.runtime.observation.mapper.ExecutionRecordMapper;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 基于 MyBatis Plus 的执行记录仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentExecutionRecordRepository implements ExecutionRecordRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentExecutionRecordRepository.class);

    /**
     * 执行记录 Mapper。
     */
    private final ExecutionRecordMapper executionRecordMapper;

    /**
     * JSON 序列化器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造执行记录持久化仓储。
     *
     * @param executionRecordMapper 执行记录 Mapper
     * @param objectMapper          JSON 序列化器
     */
    public PersistentExecutionRecordRepository(ExecutionRecordMapper executionRecordMapper,
                                               ObjectMapper objectMapper) {
        this.executionRecordMapper = executionRecordMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 保存执行记录。
     *
     * @param record 执行记录
     */
    @Override
    public void save(ExecutionRecord record) {
        executionRecordMapper.insert(toEntity(record));
        log.debug("持久化执行记录, traceId={}, capabilityCode={}, success={}",
                record.traceId(), record.capabilityCode(), record.success());
    }

    /**
     * 查询全部执行记录（按记录时间倒序）。
     *
     * @return 执行记录列表
     */
    @Override
    public List<ExecutionRecord> listAll() {
        LambdaQueryWrapper<ExecutionRecordEntity> queryWrapper = new LambdaQueryWrapper<ExecutionRecordEntity>()
                .eq(ExecutionRecordEntity::getTenantId, TenantContext.currentTenantId())
                .orderByDesc(ExecutionRecordEntity::getRecordedAt)
                .orderByDesc(ExecutionRecordEntity::getId);
        return executionRecordMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ExecutionRecord> findByTraceId(String traceId) {
        LambdaQueryWrapper<ExecutionRecordEntity> queryWrapper = new LambdaQueryWrapper<ExecutionRecordEntity>()
                .eq(ExecutionRecordEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ExecutionRecordEntity::getTraceId, traceId)
                .orderByDesc(ExecutionRecordEntity::getRecordedAt)
                .orderByDesc(ExecutionRecordEntity::getId)
                .last("LIMIT 1");
        ExecutionRecordEntity entity = executionRecordMapper.selectOne(queryWrapper);
        return entity == null ? Optional.empty() : Optional.of(toDomain(entity));
    }

    /**
     * 转换为持久化实体。
     *
     * @param record 执行记录
     * @return 实体
     */
    private ExecutionRecordEntity toEntity(ExecutionRecord record) {
        ExecutionRecordEntity entity = new ExecutionRecordEntity();
        entity.setTraceId(record.traceId());
        entity.setTenantId(TenantIds.resolveId(record.tenantCode()));
        entity.setCapabilityCode(record.capabilityCode());
        entity.setCapabilityVersion(record.capabilityVersion());
        entity.setAgentCode(record.agentCode());
        entity.setResultStatus(record.resultStatus());
        entity.setSuccess(record.success());
        entity.setFailureReason(record.failureReason());
        entity.setRecordedAt(record.recordedAt());
        entity.setInputJson(toJson(record.input()));
        entity.setRoutingJson(toJson(record.routing()));
        entity.setResultJson(toJson(record.result()));
        return entity;
    }

    /**
     * 转换为领域对象。
     *
     * @param entity 实体
     * @return 执行记录
     */
    private ExecutionRecord toDomain(ExecutionRecordEntity entity) {
        return new ExecutionRecord(
                entity.getTraceId(),
                TenantIds.toCode(entity.getTenantId()),
                entity.getCapabilityCode(),
                entity.getCapabilityVersion(),
                entity.getAgentCode(),
                entity.getResultStatus(),
                Boolean.TRUE.equals(entity.getSuccess()),
                entity.getFailureReason(),
                entity.getRecordedAt(),
                parseJson(entity.getInputJson(), ExecutionRecord.ExecutionInputDetail.class),
                parseJson(entity.getRoutingJson(), ExecutionRecord.ExecutionRoutingDetail.class),
                parseJson(entity.getResultJson(), ExecutionRecord.ExecutionResultDetail.class)
        );
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("执行记录 JSON 序列化失败", e);
            return null;
        }
    }

    private <T> T parseJson(String json, Class<T> type) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.warn("执行记录 JSON 反序列化失败, type={}", type.getSimpleName(), e);
            return null;
        }
    }
}
