package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.center.model.provider.ModelProviderDefinition;
import cn.cyc.ai.cog.center.model.provider.ProviderApiKeySupport;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 将模型主数据 + 提供商绑定展开为运行时路由定义。
 */
public final class ModelRouteResolver {

    private ModelRouteResolver() {
    }

    /**
     * 展开全部可用路由。
     */
    public static List<ModelDefinition> expandRoutes(List<ModelMasterDefinition> models,
                                                      List<ModelProviderDefinition> providers) {
        List<ModelDefinition> routes = new ArrayList<>();
        for (ModelMasterDefinition model : models) {
            routes.addAll(expandRoutes(model, providers));
        }
        return routes;
    }

    /**
     * 展开单个模型的全部绑定路由。
     */
    public static List<ModelDefinition> expandRoutes(ModelMasterDefinition model,
                                                     List<ModelProviderDefinition> providers) {
        List<ModelDefinition> routes = new ArrayList<>();
        for (ModelBindingDefinition binding : model.providerBindings()) {
            if (binding.status() != CommonStatus.ENABLED || model.status() != CommonStatus.ENABLED) {
                continue;
            }
            resolveProvider(providers, binding.providerCode()).ifPresent(provider -> {
                if (provider.status() != CommonStatus.ENABLED) {
                    return;
                }
                routes.add(toRoute(model, provider, binding));
            });
        }
        return routes;
    }

    /**
     * 按 modelCode 选取优先级最高的启用路由。
     */
    public static Optional<ModelDefinition> selectPrimaryRoute(ModelMasterDefinition model,
                                                             List<ModelProviderDefinition> providers) {
        return expandRoutes(model, providers).stream()
                .max(Comparator.comparingInt(ModelDefinition::routePriority));
    }

    private static ModelDefinition toRoute(ModelMasterDefinition model,
                                           ModelProviderDefinition provider,
                                           ModelBindingDefinition binding) {
        String endpoint = StringUtils.hasText(binding.endpoint())
                ? binding.endpoint()
                : defaultString(provider.defaultEndpoint());
        String apiKey = ProviderApiKeySupport.isEffectiveApiKey(binding.apiKey())
                ? binding.apiKey()
                : defaultString(provider.apiKey());
        return new ModelDefinition(
                provider.providerCode(),
                provider.providerName(),
                model.modelCode(),
                model.modelName(),
                model.modelType(),
                endpoint,
                apiKey,
                model.timeoutMs(),
                model.retryTimes(),
                model.status(),
                binding.routePriority(),
                model.fallbackModelCode()
        );
    }

    private static Optional<ModelProviderDefinition> resolveProvider(List<ModelProviderDefinition> providers,
                                                                       String providerCode) {
        return providers.stream()
                .filter(provider -> provider.providerCode().equals(providerCode))
                .findFirst();
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
