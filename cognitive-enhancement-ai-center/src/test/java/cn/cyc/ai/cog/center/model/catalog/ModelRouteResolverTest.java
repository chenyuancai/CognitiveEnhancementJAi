package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelRouteResolverTest {

    @Test
    void shouldUseProviderApiKeyWhenBindingHasLegacyCredentialRefPlaceholder() {
        ModelMasterDefinition model = new ModelMasterDefinition(
                "qwen-plus",
                "Qwen Plus",
                "CHAT",
                30_000,
                2,
                CommonStatus.ENABLED,
                null,
                List.of(new ModelBindingDefinition(
                        "qwen-plus",
                        "bailian",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1",
                        "__DASHSCOPE_API_KEY__",
                        20,
                        CommonStatus.ENABLED
                ))
        );
        List<ModelProviderDefinition> providers = List.of(new ModelProviderDefinition(
                "bailian",
                "阿里云百炼",
                "DASHSCOPE",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-real-dashscope-key",
                null,
                CommonStatus.ENABLED
        ));

        Optional<ModelDefinition> route = ModelRouteResolver.selectPrimaryRoute(model, providers);

        assertTrue(route.isPresent());
        assertEquals("DASHSCOPE", route.get().providerType());
        assertEquals("sk-real-dashscope-key", route.get().apiKey());
    }
}
