package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptReleaseServiceTest {

    private PromptTemplateRepository templateRepository;
    private PromptReleasePointerRepository pointerRepository;
    private PromptReleaseService releaseService;

    private final SchemaDefinition schema = new SchemaDefinition(
            "object", "input", true,
            Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
            null, List.of()
    );

    @BeforeEach
    void setUp() {
        templateRepository = new InMemoryPromptTemplateRepository();
        pointerRepository = new InMemoryPromptReleasePointerRepository();
        releaseService = new PromptReleaseService(templateRepository, pointerRepository);
        templateRepository.save(new PromptTemplate(
                "prompt.qa.default",
                "问答模板",
                "qa",
                "1.0.0",
                "问题：{{question}}",
                schema,
                schema,
                CommonStatus.ENABLED,
                Instant.parse("2026-05-11T00:00:00Z"),
                PromptLifecycleStatus.PUBLISHED
        ));
    }

    @Test
    void publish_shouldOfflinePreviousPublishedVersion() {
        releaseService.createDraft("prompt.qa.default", new PromptDraftRequest(
                "prompt.qa.default", "1.1.0", null, null, "新版：{{question}}", null, null
        ));
        PromptResult published = releaseService.publish("prompt.qa.default", new PromptPublishRequest("prompt.qa.default", "1.1.0"));

        assertEquals("1.1.0", published.version());
        assertEquals(PromptLifecycleStatus.PUBLISHED, published.lifecycleStatus());
        List<PromptResult> versions = releaseService.listVersions("prompt.qa.default");
        assertTrue(versions.stream()
                .anyMatch(item -> "1.0.0".equals(item.version())
                        && item.lifecycleStatus() == PromptLifecycleStatus.OFFLINE));
    }

    @Test
    void configureGray_shouldPersistReleasePointer() {
        releaseService.createDraft("prompt.qa.default", new PromptDraftRequest(
                "prompt.qa.default", "1.1.0", null, null, "灰度版：{{question}}", null, null
        ));
        releaseService.publish("prompt.qa.default", new PromptPublishRequest("prompt.qa.default", "1.1.0"));
        PromptReleasePointer pointer = releaseService.configureGray("prompt.qa.default", new PromptGrayRequest(
                "prompt.qa.default", new PromptGrayRule("1.0.0", "1.1.0", 20)
        ));

        assertEquals("1.0.0", pointer.baselineVersion());
        assertEquals("1.1.0", pointer.candidateVersion());
        assertEquals(20, pointer.grayRule().percentage());
    }
}
