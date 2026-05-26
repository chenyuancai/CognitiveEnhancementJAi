package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.support.CenterResponses;
import cn.cyc.ai.cog.center.support.DefinitionListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型后台管理接口，负责处理模型定义的查询与维护请求。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/center/models")
public class ModelAdminController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ModelAdminController.class);

    /**
     * 模型后台管理服务。
     */
    private final ModelAdminService modelAdminService;

    /**
     * 创建模型后台管理控制器。
     *
     * @param modelAdminService 模型后台管理服务
     */
    public ModelAdminController(ModelAdminService modelAdminService) {
        this.modelAdminService = modelAdminService;
    }

    /**
     * 查询全部模型定义。
     *
     * @return 模型定义列表
     */
    @GetMapping
    public ApiResponse<DefinitionListResult<ModelResult>> listAll() {
        log.info("收到查询全部模型定义请求");
        return CenterResponses.success(modelAdminService.listAll());
    }

    /**
     * 按编码查询模型定义详情。
     *
     * @param code 模型编码
     * @return 模型定义详情
     */
    @GetMapping("/{code}")
    public ApiResponse<ModelResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询模型定义详情请求，code={}", code);
        return CenterResponses.success(modelAdminService.getByCode(code));
    }

    /**
     * 创建模型定义。
     *
     * @param request 模型创建请求
     * @return 创建后的模型定义
     */
    @PostMapping
    public ApiResponse<ModelResult> create(@RequestBody ModelUpsertRequest request) {
        log.info("收到创建模型定义请求，modelCode={}", request.modelCode());
        return CenterResponses.success(modelAdminService.create(request));
    }

    /**
     * 更新指定模型定义。
     *
     * @param code    模型编码
     * @param request 模型更新请求
     * @return 更新后的模型定义
     */
    @PutMapping("/{code}")
    public ApiResponse<ModelResult> update(@PathVariable("code") String code, @RequestBody ModelUpsertRequest request) {
        log.info("收到更新模型定义请求，code={}", code);
        return CenterResponses.success(modelAdminService.update(code, request));
    }
}
