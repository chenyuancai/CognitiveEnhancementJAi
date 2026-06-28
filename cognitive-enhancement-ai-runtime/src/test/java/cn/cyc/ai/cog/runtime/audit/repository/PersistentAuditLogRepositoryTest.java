package cn.cyc.ai.cog.runtime.audit.repository;

import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.entity.AuditLogEntity;
import cn.cyc.ai.cog.runtime.audit.mapper.AuditLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 审计日志持久化仓储测试。
 *
 * @author cyc
 */
class PersistentAuditLogRepositoryTest {

    private AuditLogMapper mapper;
    private PersistentAuditLogRepository repository;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mapper = mock(AuditLogMapper.class);
        repository = new PersistentAuditLogRepository(mapper, objectMapper);
    }

    @Test
    void shouldMapDomainToEntityWhenSave() {
        Instant recordedAt = Instant.parse("2026-06-10T00:00:00Z");
        AuditLogRecord record = new AuditLogRecord(
                "tenant-a",
                "trace-audit",
                "RUNTIME_INVOCATION",
                "EXECUTE_SUCCESS",
                "CapabilityDefinition",
                "capability.audit",
                "system",
                true,
                Map.of("resultStatus", "LLM_GENERATED"),
                recordedAt
        );

        repository.save(record);

        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(mapper).insert(captor.capture());
        AuditLogEntity entity = captor.getValue();
        assertEquals(1L, entity.getTenantId());
        assertEquals("trace-audit", entity.getTraceId());
        assertEquals("RUNTIME_INVOCATION", entity.getEventType());
        assertEquals("EXECUTE_SUCCESS", entity.getAction());
        assertEquals("CapabilityDefinition", entity.getResourceType());
        assertEquals("capability.audit", entity.getResourceCode());
        assertEquals("system", entity.getOperator());
        assertEquals(true, entity.getSuccess());
        assertEquals(recordedAt, entity.getRecordedAt());
        assertTrue(entity.getDetailJson().contains("LLM_GENERATED"));
    }

    @Test
    void shouldMapEntityToDomainWhenListAll() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setTenantId(1L);
        entity.setTraceId("trace-config");
        entity.setEventType("CONFIG_CHANGE");
        entity.setAction("CREATE");
        entity.setResourceType("ModelDefinition");
        entity.setResourceCode("gpt-4o-mini");
        entity.setOperator("system");
        entity.setSuccess(true);
        entity.setDetailJson("{\"resourceName\":\"GPT-4o mini\"}");
        entity.setRecordedAt(Instant.parse("2026-06-10T01:00:00Z"));
        when(mapper.selectList(any())).thenReturn(List.of(entity));

        List<AuditLogRecord> records = repository.listAll();

        assertEquals(1, records.size());
        AuditLogRecord record = records.get(0);
        assertEquals("CONFIG_CHANGE", record.eventType());
        assertEquals("CREATE", record.action());
        assertEquals("ModelDefinition", record.resourceType());
        assertEquals("gpt-4o-mini", record.resourceCode());
        assertEquals("GPT-4o mini", record.detail().get("resourceName"));
    }
}
