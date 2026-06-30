package cn.cyc.ai.cog.runtime.model.registry;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型路由运行时刷新：Center 元数据变更后同步 LLM 注册表。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ModelRuntimeRefreshService {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(ModelRuntimeRefreshService.class);

    /** 模型Definition仓储。 */
    private final ModelDefinitionRepository modelDefinitionRepository;
    /** llmRouteRegistry。 */
    private final LlmRouteRegistry llmRouteRegistry;

    /**
     * 创建模型运行时Refresh服务。
     */
    public ModelRuntimeRefreshService(ModelDefinitionRepository modelDefinitionRepository,
                                      LlmRouteRegistry llmRouteRegistry) {
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.llmRouteRegistry = llmRouteRegistry;
    }

    /**
     * 执行refresh。
     */
    public void refresh() {
        List<ModelDefinition> routes = modelDefinitionRepository.listAll();
        llmRouteRegistry.replaceAll(routes);
        log.info("LLM 路由注册表已刷新, routeCount={}", llmRouteRegistry.size());
    }
}
