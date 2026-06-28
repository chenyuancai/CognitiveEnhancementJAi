package cn.cyc.ai.cog.runtime.release.router;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CapabilityReleaseRouterTest {

    @Test
    void shouldReturnBaselineWhenGrayDisabled() {
        CapabilityReleasePointerRepository repository = mock(CapabilityReleasePointerRepository.class);
        when(repository.findByCapabilityCode("capability.qa.answer")).thenReturn(java.util.Optional.of(
                new CapabilityReleasePointer("default", "capability.qa.answer", "1.0.0", null, null)
        ));
        CapabilityReleaseRouter router = new CapabilityReleaseRouter(repository);
        assertEquals("1.0.0", router.resolveVersion("capability.qa.answer", "trace-1", "1.0.0"));
    }

    @Test
    void shouldRouteCandidateByTraceHash() {
        CapabilityReleasePointerRepository repository = mock(CapabilityReleasePointerRepository.class);
        PromptGrayRule grayRule = new PromptGrayRule("1.0.0", "1.0.1", 100);
        when(repository.findByCapabilityCode("capability.qa.answer")).thenReturn(java.util.Optional.of(
                new CapabilityReleasePointer("default", "capability.qa.answer", "1.0.0", "1.0.1", grayRule)
        ));
        CapabilityReleaseRouter router = new CapabilityReleaseRouter(repository);
        assertEquals("1.0.1", router.resolveVersion("capability.qa.answer", "trace-gray", "1.0.0"));
    }
}
