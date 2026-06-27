package cn.cyc.ai.cog.runtime.model.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动时预注册全部 LLM 路由（对齐 zcloud ModelRuntimeConfigLoader）。
 */
@Component
@Order(100)
public class ModelRuntimeBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ModelRuntimeBootstrap.class);

    private final ModelRuntimeRefreshService modelRuntimeRefreshService;

    public ModelRuntimeBootstrap(ModelRuntimeRefreshService modelRuntimeRefreshService) {
        this.modelRuntimeRefreshService = modelRuntimeRefreshService;
    }

    @Override
    public void run(ApplicationArguments args) {
        modelRuntimeRefreshService.refresh();
        log.info("ModelRuntimeBootstrap 完成 LLM 路由预注册");
    }
}
