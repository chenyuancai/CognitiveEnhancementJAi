package cn.cyc.ai.cog.runtime.trace.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanStatus;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.entity.TraceSpanEntity;
import cn.cyc.ai.cog.runtime.trace.mapper.TraceSpanMapper;
import cn.cyc.ai.cog.runtime.trace.spi.TraceSpanRepository;
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
 * 持久化 TraceSpan 仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentTraceSpanRepository implements TraceSpanRepository {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(PersistentTraceSpanRepository.class);

    /** 链路SpanMapper。 */
    private final TraceSpanMapper traceSpanMapper;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建Persistent链路Span仓储。
     *
     * @param traceSpanMapper 链路SpanMapper
     * @param objectMapper JSON 序列化器
     */
    public PersistentTraceSpanRepository(TraceSpanMapper traceSpanMapper, ObjectMapper objectMapper) {
        this.traceSpanMapper = traceSpanMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行save。
     *
     * @param span span
     */
    @Override
    public void save(TraceSpan span) {
        traceSpanMapper.insert(toEntity(span));
        log.debug("持久化 TraceSpan, traceId={}, spanId={}", span.traceId(), span.spanId());
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<TraceSpan> listAll() {
        LambdaQueryWrapper<TraceSpanEntity> wrapper = new LambdaQueryWrapper<TraceSpanEntity>()
                .eq(TraceSpanEntity::getTenantId, TenantContext.currentTenantId())
                .orderByDesc(TraceSpanEntity::getRecordedAt)
                .orderByDesc(TraceSpanEntity::getId);
        return traceSpanMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 查找人链路ID。
     *
     * @param traceId 链路 Trace ID
     * @return 查找结果
     */
    @Override
    public List<TraceSpan> findByTraceId(String traceId) {
        LambdaQueryWrapper<TraceSpanEntity> wrapper = new LambdaQueryWrapper<TraceSpanEntity>()
                .eq(TraceSpanEntity::getTenantId, TenantContext.currentTenantId())
                .eq(TraceSpanEntity::getTraceId, traceId)
                .orderByAsc(TraceSpanEntity::getRecordedAt)
                .orderByAsc(TraceSpanEntity::getId);
        return traceSpanMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 转换为实体。
     *
     * @param span span
     * @return 转换结果
     */
    private TraceSpanEntity toEntity(TraceSpan span) {
        TraceSpanEntity entity = new TraceSpanEntity();
        entity.setTenantId(TenantIds.resolveId(span.tenantCode()));
        entity.setTraceId(span.traceId());
        entity.setSpanId(span.spanId());
        entity.setParentSpanId(span.parentSpanId());
        entity.setSpanType(span.spanType().name());
        entity.setSpanName(span.spanName());
        entity.setStatus(span.status().name());
        entity.setLatencyMs(span.latencyMs());
        entity.setAttributesJson(toJson(span.attributes()));
        entity.setErrorStack(span.errorStack());
        entity.setRecordedAt(span.recordedAt());
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private TraceSpan toDomain(TraceSpanEntity entity) {
        return new TraceSpan(
                TenantIds.toCode(entity.getTenantId()),
                entity.getTraceId(),
                entity.getSpanId(),
                entity.getParentSpanId(),
                TraceSpanType.valueOf(entity.getSpanType()),
                entity.getSpanName(),
                TraceSpanStatus.valueOf(entity.getStatus()),
                entity.getLatencyMs() == null ? 0L : entity.getLatencyMs(),
                parseAttributes(entity.getAttributesJson()),
                entity.getErrorStack(),
                entity.getRecordedAt()
        );
    }

    /**
     * 转换为JSON。
     *
     * @param attributes attributes
     * @return 转换结果
     */
    private String toJson(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException exception) {
            log.warn("TraceSpan attributes JSON 序列化失败", exception);
            return "{}";
        }
    }

    private Map<String, Object> parseAttributes(String attributesJson) {
        if (attributesJson == null || attributesJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(attributesJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            log.warn("TraceSpan attributes JSON 反序列化失败", exception);
            return Map.of();
        }
    }
}
