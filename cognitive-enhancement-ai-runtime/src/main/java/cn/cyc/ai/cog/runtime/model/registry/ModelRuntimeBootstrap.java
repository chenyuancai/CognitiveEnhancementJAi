package cn.cyc.ai.cog.runtime.model.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动时预注册全部 LLM 路由（对齐 zcloud ModelRuntimeConfigLoader）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@Order(100)
public class ModelRuntimeBootstrap implements ApplicationRunner {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(ModelRuntimeBootstrap.class);

    /** 模型运行时Refresh服务。 */
    private final ModelRuntimeRefreshService modelRuntimeRefreshService;

    /**
     * 创建ModelRuntimeBootstrap。
     *
     * @param modelRuntimeRefreshService 模型运行时Refresh服务
     */
    public ModelRuntimeBootstrap(ModelRuntimeRefreshService modelRuntimeRefreshService) {
        this.modelRuntimeRefreshService = modelRuntimeRefreshService;
    }

    /**
     * 执行操作。
     *
     * @param args args
     * @return 执行结果
     */
    @Override
    public void run(ApplicationArguments args) {
        modelRuntimeRefreshService.refresh();
        log.info("ModelRuntimeBootstrap 完成 LLM 路由预注册");
    }
}
