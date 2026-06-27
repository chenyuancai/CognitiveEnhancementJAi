package cn.cyc.ai.cog.runtime.model.registry;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM 路由运行时注册表：启动时预加载全部 model×provider 路由，供治理与 ReAct 快速解析。
 */
@Component
public class LlmRouteRegistry {

    private final Map<String, ModelDefinition> routes = new ConcurrentHashMap<>();

    public void replaceAll(Collection<ModelDefinition> definitions) {
        routes.clear();
        if (definitions == null) {
            return;
        }
        for (ModelDefinition definition : definitions) {
            routes.put(routeKey(definition), definition);
        }
    }

    public Optional<ModelDefinition> findRoute(String modelCode, String providerCode) {
        return Optional.ofNullable(routes.get(routeKey(modelCode, providerCode)));
    }

    public Optional<ModelDefinition> findPrimaryRoute(String modelCode) {
        return routes.values().stream()
                .filter(route -> route.modelCode().equals(modelCode))
                .max(Comparator.comparingInt(ModelDefinition::routePriority));
    }

    public List<ModelDefinition> listRoutes() {
        return routes.values().stream()
                .sorted(Comparator.comparing(ModelDefinition::modelCode)
                        .thenComparing(ModelDefinition::routePriority))
                .toList();
    }

    public int size() {
        return routes.size();
    }

    public static String routeKey(ModelDefinition definition) {
        return routeKey(definition.modelCode(), definition.providerCode());
    }

    public static String routeKey(String modelCode, String providerCode) {
        return modelCode + "@" + providerCode;
    }
}
