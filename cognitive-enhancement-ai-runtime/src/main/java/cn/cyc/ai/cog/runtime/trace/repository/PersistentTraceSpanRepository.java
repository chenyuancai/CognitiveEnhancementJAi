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
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentTraceSpanRepository implements TraceSpanRepository {

    private static final Logger log = LoggerFactory.getLogger(PersistentTraceSpanRepository.class);

    private final TraceSpanMapper traceSpanMapper;
    private final ObjectMapper objectMapper;

    public PersistentTraceSpanRepository(TraceSpanMapper traceSpanMapper, ObjectMapper objectMapper) {
        this.traceSpanMapper = traceSpanMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(TraceSpan span) {
        traceSpanMapper.insert(toEntity(span));
        log.debug("持久化 TraceSpan, traceId={}, spanId={}", span.traceId(), span.spanId());
    }

    @Override
    public List<TraceSpan> listAll() {
        LambdaQueryWrapper<TraceSpanEntity> wrapper = new LambdaQueryWrapper<TraceSpanEntity>()
                .eq(TraceSpanEntity::getTenantId, TenantContext.currentTenantId())
                .orderByDesc(TraceSpanEntity::getRecordedAt)
                .orderByDesc(TraceSpanEntity::getId);
        return traceSpanMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<TraceSpan> findByTraceId(String traceId) {
        LambdaQueryWrapper<TraceSpanEntity> wrapper = new LambdaQueryWrapper<TraceSpanEntity>()
                .eq(TraceSpanEntity::getTenantId, TenantContext.currentTenantId())
                .eq(TraceSpanEntity::getTraceId, traceId)
                .orderByAsc(TraceSpanEntity::getRecordedAt)
                .orderByAsc(TraceSpanEntity::getId);
        return traceSpanMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

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
