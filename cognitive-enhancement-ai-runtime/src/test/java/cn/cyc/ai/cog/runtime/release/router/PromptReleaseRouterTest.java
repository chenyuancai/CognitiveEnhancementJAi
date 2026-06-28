package cn.cyc.ai.cog.runtime.release.router;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PromptReleaseRouterTest {

    @Test
    void resolveVersion_shouldReturnBaselineWhenGrayDisabled() {
        PromptReleasePointerRepository repository = mock(PromptReleasePointerRepository.class);
        when(repository.findByPromptCode("prompt.qa.default")).thenReturn(Optional.of(
                new PromptReleasePointer("default", "prompt.qa.default", "1.0.0", null, null)
        ));
        PromptReleaseRouter router = new PromptReleaseRouter(repository);

        assertEquals("1.0.0", router.resolveVersion("prompt.qa.default", "trace-1", "1.0.0"));
    }

    @Test
    void resolveVersion_shouldRouteByTraceHash() {
        PromptReleasePointerRepository repository = mock(PromptReleasePointerRepository.class);
        PromptGrayRule grayRule = new PromptGrayRule("1.0.0", "1.1.0", 100);
        when(repository.findByPromptCode("prompt.qa.default")).thenReturn(Optional.of(
                new PromptReleasePointer("default", "prompt.qa.default", "1.0.0", "1.1.0", grayRule)
        ));
        PromptReleaseRouter router = new PromptReleaseRouter(repository);

        assertEquals("1.1.0", router.resolveVersion("prompt.qa.default", "trace-gray", "1.0.0"));
    }
}
