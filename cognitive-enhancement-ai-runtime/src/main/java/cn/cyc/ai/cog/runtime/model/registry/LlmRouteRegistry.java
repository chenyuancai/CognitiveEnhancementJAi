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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class LlmRouteRegistry {

    private final Map<String, ModelDefinition> routes = new ConcurrentHashMap<>();

    /**
     * 执行replaceAll。
     *
     * @param definitions definitions
     */
    public void replaceAll(Collection<ModelDefinition> definitions) {
        routes.clear();
        if (definitions == null) {
            return;
        }
        for (ModelDefinition definition : definitions) {
            routes.put(routeKey(definition), definition);
        }
    }

    /**
     * 查找Route。
     *
     * @param modelCode 模型编码
     * @param providerCode 提供者编码
     * @return 查找结果
     */
    public Optional<ModelDefinition> findRoute(String modelCode, String providerCode) {
        return Optional.ofNullable(routes.get(routeKey(modelCode, providerCode)));
    }

    /**
     * 查找PrimaryRoute。
     *
     * @param modelCode 模型编码
     * @return 查找结果
     */
    public Optional<ModelDefinition> findPrimaryRoute(String modelCode) {
        return routes.values().stream()
                .filter(route -> route.modelCode().equals(modelCode))
                .max(Comparator.comparingInt(ModelDefinition::routePriority));
    }

    /**
     * 查询Routes列表。
     * @return 结果列表
     */
    public List<ModelDefinition> listRoutes() {
        return routes.values().stream()
                .sorted(Comparator.comparing(ModelDefinition::modelCode)
                        .thenComparing(ModelDefinition::routePriority))
                .toList();
    }

    /**
     * 执行大小。
     * @return 执行结果
     */
    public int size() {
        return routes.size();
    }

    /**
     * 执行route键。
     *
     * @param definition definition
     * @return 执行结果
     */
    public static String routeKey(ModelDefinition definition) {
        return routeKey(definition.modelCode(), definition.providerCode());
    }

    /**
     * 执行route键。
     *
     * @param modelCode 模型编码
     * @param providerCode 提供者编码
     * @return 执行结果
     */
    public static String routeKey(String modelCode, String providerCode) {
        return modelCode + "@" + providerCode;
    }
}
