package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CapabilityReleaseServiceTest {

    private CapabilityDefinitionRepository definitionRepository;
    private CapabilityReleasePointerRepository pointerRepository;
    private InMemoryCapabilityTenantBindingRepository tenantBindingRepository;
    private CapabilityReleaseService releaseService;

    private final SchemaDefinition schema = new SchemaDefinition(
            "object", "input", true,
            Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
            null, List.of()
    );

    @BeforeEach
    void setUp() {
        definitionRepository = new InMemoryCapabilityDefinitionRepository();
        pointerRepository = new InMemoryCapabilityReleasePointerRepository();
        tenantBindingRepository = new InMemoryCapabilityTenantBindingRepository();
        releaseService = new CapabilityReleaseService(
                definitionRepository, pointerRepository, tenantBindingRepository);

        definitionRepository.save(new CapabilityDefinition(
                "capability.qa.answer", "问答", "desc", schema, schema,
                Map.of(), ExecutionMode.SYNC, "agent.qa", RiskLevel.LOW, false, CommonStatus.ENABLED
        ));
    }

    @Test
    void shouldPublishDraftAndConfigureGray() {
        CapabilityResult draft = releaseService.createDraft("capability.qa.answer", new CapabilityDraftRequest(
                "capability.qa.answer", null, "问答 v2", null, null, null, null, null, null, null, null));
        assertEquals("1.0.1", draft.version());
        assertEquals(CapabilityLifecycleStatus.DRAFT, draft.lifecycleStatus());

        CapabilityResult published = releaseService.publish("capability.qa.answer",
                new CapabilityPublishRequest("capability.qa.answer", "1.0.1"));
        assertEquals(CapabilityLifecycleStatus.PUBLISHED, published.lifecycleStatus());

        CapabilityReleasePointer pointer = releaseService.configureGray("capability.qa.answer",
                new CapabilityGrayRequest("capability.qa.answer", new PromptGrayRule("1.0.0", "1.0.1", 20)));
        assertEquals("1.0.0", pointer.baselineVersion());
        assertEquals("1.0.1", pointer.candidateVersion());

        List<CapabilityResult> versions = releaseService.listVersions("capability.qa.answer");
        assertEquals(2, versions.size());
    }

    @Test
    void shouldDisableCapabilityForTenant() {
        var binding = releaseService.configureTenant("capability.qa.answer", "tenant-b",
                new CapabilityTenantBindingRequest("capability.qa.answer", "tenant-b", false));
        assertFalse(binding.enabled());
    }
}
