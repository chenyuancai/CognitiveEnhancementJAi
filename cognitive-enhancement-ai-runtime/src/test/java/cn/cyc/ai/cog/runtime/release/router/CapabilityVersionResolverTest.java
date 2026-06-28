package cn.cyc.ai.cog.runtime.release.router;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBinding;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBindingRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CapabilityVersionResolverTest {

    private final SchemaDefinition schema = new SchemaDefinition(
            "object", "input", true,
            Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
            null, List.of()
    );

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldRejectDisabledTenantCapability() {
        TenantContext.setTenantCode("tenant-b");
        CapabilityDefinitionRepository definitionRepository = mock(CapabilityDefinitionRepository.class);
        CapabilityReleaseRouter releaseRouter = mock(CapabilityReleaseRouter.class);
        CapabilityTenantBindingRepository tenantBindingRepository = mock(CapabilityTenantBindingRepository.class);

        when(tenantBindingRepository.findByTenantAndCapability("tenant-b", "capability.qa.answer"))
                .thenReturn(Optional.of(new CapabilityTenantBinding("tenant-b", "capability.qa.answer", false)));

        CapabilityVersionResolver resolver = new CapabilityVersionResolver(
                definitionRepository, releaseRouter, tenantBindingRepository);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> resolver.resolve("capability.qa.answer", "trace-1"));
        assertEquals("CAPABILITY_DISABLED", exception.getSemanticCode());
    }

    @Test
    void shouldResolvePublishedVersion() {
        CapabilityDefinition published = new CapabilityDefinition(
                "capability.qa.answer", "问答", "desc", schema, schema,
                Map.of(), ExecutionMode.SYNC, "agent.qa", RiskLevel.LOW, false, CommonStatus.ENABLED
        );
        CapabilityDefinitionRepository definitionRepository = mock(CapabilityDefinitionRepository.class);
        CapabilityReleaseRouter releaseRouter = mock(CapabilityReleaseRouter.class);
        CapabilityTenantBindingRepository tenantBindingRepository = mock(CapabilityTenantBindingRepository.class);

        when(definitionRepository.findPublishedByCapabilityCode("capability.qa.answer"))
                .thenReturn(Optional.of(published));
        when(releaseRouter.resolveVersion("capability.qa.answer", "trace-1", "1.0.0"))
                .thenReturn("1.0.0");
        when(definitionRepository.findByCapabilityCodeAndVersion("capability.qa.answer", "1.0.0"))
                .thenReturn(Optional.of(published));

        CapabilityVersionResolver resolver = new CapabilityVersionResolver(
                definitionRepository, releaseRouter, tenantBindingRepository);

        CapabilityDefinition resolved = resolver.resolve("capability.qa.answer", "trace-1");
        assertEquals("1.0.0", resolved.version());
    }
}
