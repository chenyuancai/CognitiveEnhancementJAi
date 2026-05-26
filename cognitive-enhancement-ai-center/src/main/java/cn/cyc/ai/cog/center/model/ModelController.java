package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.ListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 模型主链路接口，负责对外暴露模型定义的读写入口。
 *
 * @author cyc
 */
@RequestMapping("/api/center/models")
public class ModelController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ModelController.class);

    /**
     * 模型主链路服务。
     */
    private final ModelCenterService service;

    /**
     * 创建模型主链路控制器。
     *
     * @param service 模型主链路服务
     */
    public ModelController(ModelCenterService service) {
        this.service = service;
    }

    /**
     * 查询全部模型定义。
     *
     * @return 模型列表
     */
    @GetMapping
    public ApiResponse<ListResponse<ModelDtos.Result>> list() {
        log.info("收到主链路查询全部模型定义请求");
        return ApiResponse.success(service.list());
    }

    /**
     * 按编码查询模型定义详情。
     *
     * @param modelCode 模型编码
     * @return 模型定义详情
     */
    @GetMapping("/{modelCode}")
    public ApiResponse<ModelDtos.Result> get(@PathVariable("modelCode") String modelCode) {
        log.info("收到主链路查询模型定义详情请求，modelCode={}", modelCode);
        return ApiResponse.success(service.get(modelCode));
    }

    /**
     * 创建模型定义。
     *
     * @param request 模型创建请求
     * @return 创建后的模型定义
     */
    @PostMapping
    public ApiResponse<ModelDtos.Result> create(@RequestBody ModelDtos.CreateRequest request) {
        log.info("收到主链路创建模型定义请求，modelCode={}", request.modelCode());
        return ApiResponse.success(service.create(request));
    }

    /**
     * 更新指定模型定义。
     *
     * @param modelCode 模型编码
     * @param request   模型更新请求
     * @return 更新后的模型定义
     */
    @PutMapping("/{modelCode}")
    public ApiResponse<ModelDtos.Result> update(
            @PathVariable("modelCode") String modelCode,
            @RequestBody ModelDtos.UpdateRequest request
    ) {
        log.info("收到主链路更新模型定义请求，modelCode={}", modelCode);
        return ApiResponse.success(service.update(modelCode, request));
    }
}
