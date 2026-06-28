package cn.cyc.ai.cog.runtime.model.registry;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmRouteRegistryTest {

    @Test
    void shouldRegisterAndFindPrimaryRoute() {
        LlmRouteRegistry registry = new LlmRouteRegistry();
        ModelDefinition lowPriority = route("qwen-plus", "bailian", 10);
        ModelDefinition highPriority = route("qwen-plus", "openai", 20);
        registry.replaceAll(List.of(lowPriority, highPriority));

        assertEquals(2, registry.size());
        assertTrue(registry.findPrimaryRoute("qwen-plus").isPresent());
        assertEquals("openai", registry.findPrimaryRoute("qwen-plus").orElseThrow().providerCode());
    }

    private static ModelDefinition route(String modelCode, String providerCode, int priority) {
        return new ModelDefinition(
                providerCode,
                providerCode,
                modelCode,
                modelCode,
                "CHAT",
                "https://example.com/v1",
                "sk-test",
                30000,
                1,
                CommonStatus.ENABLED,
                priority,
                null
        );
    }
}
