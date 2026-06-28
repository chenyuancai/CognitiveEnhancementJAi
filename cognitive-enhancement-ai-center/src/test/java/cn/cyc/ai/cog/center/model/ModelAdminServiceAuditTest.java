package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.center.model.catalog.InMemoryModelCatalogRepository;
import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.provider.CatalogModelProviderRepository;
import cn.cyc.ai.cog.center.model.provider.ModelProviderAdminService;
import cn.cyc.ai.cog.center.model.provider.ModelProviderUpsertRequest;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.repository.InMemoryAuditLogRepository;
import cn.cyc.ai.cog.runtime.audit.service.LoggingAuditRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 模型管理审计测试。
 */
class ModelAdminServiceAuditTest {

    private ModelAdminService service;

    @BeforeEach
    void setUp() {
        ModelCatalogRepository catalogRepository = new InMemoryModelCatalogRepository();
        CatalogModelProviderRepository providerRepository = new CatalogModelProviderRepository(catalogRepository);
        ModelProviderAdminService providerAdminService = new ModelProviderAdminService(providerRepository, catalogRepository);
        providerAdminService.seed(new ModelProviderUpsertRequest(
                "openai", "OpenAI", "OPENAI_COMPATIBLE",
                "https://api.openai.com/v1", "OPENAI_API_KEY", null, CommonStatus.ENABLED));
        service = new ModelAdminService(catalogRepository);
        service.setAuditRecorder(new LoggingAuditRecorder(new InMemoryAuditLogRepository()));
    }

    @Test
    void shouldRecordConfigChangeAuditWhenCreateModel() {
        InMemoryAuditLogRepository auditLogRepository = new InMemoryAuditLogRepository();
        service.setAuditRecorder(new LoggingAuditRecorder(auditLogRepository));

        service.create(request("gpt-4o-mini"));

        List<AuditLogRecord> records = auditLogRepository.listAll();
        assertEquals(1, records.size());
        AuditLogRecord record = records.get(0);
        assertEquals("CONFIG_CHANGE", record.eventType());
        assertEquals("CREATE", record.action());
        assertEquals("ModelDefinition", record.resourceType());
        assertEquals("gpt-4o-mini", record.resourceCode());
        assertEquals(true, record.success());
    }

    private ModelUpsertRequest request(String modelCode) {
        return new ModelUpsertRequest(
                "openai",
                "OpenAI",
                modelCode,
                "GPT-4o mini",
                "chat",
                "https://api.openai.com/v1",
                "OPENAI_API_KEY",
                30000,
                1,
                CommonStatus.ENABLED,
                100,
                null,
                null
        );
    }
}
