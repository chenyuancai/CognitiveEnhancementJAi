package cn.cyc.ai.cog.center;

import cn.cyc.ai.cog.center.capability.DbCapabilityDefinitionRepository;
import cn.cyc.ai.cog.center.model.CatalogModelDefinitionRepository;
import cn.cyc.ai.cog.center.model.catalog.DbModelCatalogRepository;
import cn.cyc.ai.cog.center.model.catalog.ModelCatalogRepository;
import cn.cyc.ai.cog.center.model.provider.ModelProviderUpsertRequest;
import cn.cyc.ai.cog.center.model.provider.ModelProviderAdminService;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Center 元数据 MySQL/H2 持久化仓储集成测试（cog.persistence.enabled=true）。
 */
@SpringBootTest
@TestPropertySource(properties = {
        "cog.persistence.enabled=true",
        "cog.seed.enabled=false",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/center-it-schema.sql",
        "spring.task.scheduling.enabled=false"
})
class CenterMetadataPersistenceIntegrationTest {

    @Autowired
    private ModelDefinitionRepository modelDefinitionRepository;

    @Autowired
    private ModelCatalogRepository modelCatalogRepository;

    @Autowired
    private ModelProviderAdminService modelProviderAdminService;

    @Autowired
    private CapabilityDefinitionRepository capabilityDefinitionRepository;

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void shouldUseDbRepositories() {
        assertThat(modelDefinitionRepository).isInstanceOf(CatalogModelDefinitionRepository.class);
        assertThat(modelCatalogRepository).isInstanceOf(DbModelCatalogRepository.class);
        assertThat(capabilityDefinitionRepository).isInstanceOf(DbCapabilityDefinitionRepository.class);
    }

    @Test
    void shouldPersistModelDefinition() {
        TenantContext.setTenantId(1L);
        modelProviderAdminService.create(new ModelProviderUpsertRequest(
                "openai", "OpenAI", "OPENAI_COMPATIBLE",
                "https://api.example.com/v1/chat/completions", "sk-test-key", null, CommonStatus.ENABLED));
        modelDefinitionRepository.save(new ModelDefinition(
                "openai",
                "OpenAI",
                "it.persist.model",
                "Persist Model",
                "CHAT",
                "https://api.example.com/v1/chat/completions",
                "sk-test-key",
                30000,
                1,
                CommonStatus.ENABLED,
                10,
                null
        ));

        assertThat(modelDefinitionRepository.findByCode("it.persist.model")).isPresent();
    }

    @Test
    void shouldPersistCapabilityDefinition() {
        TenantContext.setTenantId(1L);
        SchemaDefinition schema = new SchemaDefinition("object", "input", true, Map.of(), null, List.of());
        capabilityDefinitionRepository.save(new CapabilityDefinition(
                "it.persist.capability",
                "Persist Capability",
                "integration test",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.qa",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED,
                "1.0.0",
                null,
                CapabilityLifecycleStatus.PUBLISHED
        ));

        assertThat(capabilityDefinitionRepository.findPublishedByCapabilityCode("it.persist.capability"))
                .isPresent();
    }
}
