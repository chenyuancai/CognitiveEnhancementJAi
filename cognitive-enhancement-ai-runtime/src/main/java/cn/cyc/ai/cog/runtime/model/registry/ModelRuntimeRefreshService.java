package cn.cyc.ai.cog.runtime.model.registry;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型路由运行时刷新：Center 元数据变更后同步 LLM 注册表。
 */
@Service
public class ModelRuntimeRefreshService {

    private static final Logger log = LoggerFactory.getLogger(ModelRuntimeRefreshService.class);

    private final ModelDefinitionRepository modelDefinitionRepository;
    private final LlmRouteRegistry llmRouteRegistry;

    public ModelRuntimeRefreshService(ModelDefinitionRepository modelDefinitionRepository,
                                      LlmRouteRegistry llmRouteRegistry) {
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.llmRouteRegistry = llmRouteRegistry;
    }

    public void refresh() {
        List<ModelDefinition> routes = modelDefinitionRepository.listAll();
        llmRouteRegistry.replaceAll(routes);
        log.info("LLM 路由注册表已刷新, routeCount={}", llmRouteRegistry.size());
    }
}
