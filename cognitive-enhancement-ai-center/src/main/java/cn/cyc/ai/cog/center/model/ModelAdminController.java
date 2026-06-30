package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.CenterResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型后台管理接口，负责处理模型定义的查询与维护请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Center - Model", description = "模型元数据管理：Provider、Endpoint、降级与路由优先级")
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
    @Operation(summary = "分页查询模型列表", description = "支持 providerCode/modelType/status/keyword 等条件分页查询模型定义。")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<ModelResult>> listAll(@RequestBody ModelPageQuery query) {
        log.info("收到查询全部模型定义请求");
        return CenterResponses.success(modelAdminService.listPage(query));
    }

    /**
     * 按编码查询模型定义详情。
     *
     * @param code 模型编码
     * @return 模型定义详情
     */
    @Operation(summary = "查询模型详情", description = "按 modelCode 返回模型定义。")
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
    @Operation(summary = "创建模型", description = "新建模型定义；modelCode 唯一，创建后不可修改。")
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
    @Operation(summary = "更新模型", description = "按 modelCode 更新模型定义。")
    @PostMapping("/update")
    public ApiResponse<ModelResult> update(@RequestBody ModelUpsertRequest request) {
        log.info("收到更新模型定义请求，code={}", request.modelCode());
        return CenterResponses.success(modelAdminService.update(request));
    }
}
