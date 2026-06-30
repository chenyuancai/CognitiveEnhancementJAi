package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.CenterResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模型提供商后台管理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Center - ModelProvider", description = "模型提供商元数据管理")
@RestController
@RequestMapping("/api/center/model-providers")
public class ModelProviderAdminController {

    /** 模型提供者管理后台服务。 */
    private final ModelProviderAdminService modelProviderAdminService;

    /**
     * 创建模型提供者管理后台接口。
     *
     * @param modelProviderAdminService 模型提供者管理后台服务
     */
    public ModelProviderAdminController(ModelProviderAdminService modelProviderAdminService) {
        this.modelProviderAdminService = modelProviderAdminService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "分页查询模型提供商")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<ModelProviderResult>> page(@RequestBody ModelProviderPageQuery query) {
        return CenterResponses.success(modelProviderAdminService.listPage(query));
    }

    /**
     * 执行all是否启用。
     * @return 执行结果
     */
    @Operation(summary = "查询全部启用的模型提供商", description = "供模型表单下拉选择")
    @GetMapping("/all")
    public ApiResponse<List<ModelProviderResult>> allEnabled() {
        return CenterResponses.success(modelProviderAdminService.listAllEnabled());
    }

    /**
     * 执行detail。
     *
     * @param providerCode 提供者编码
     * @return 执行结果
     */
    @Operation(summary = "查询模型提供商详情")
    @GetMapping("/{providerCode}")
    public ApiResponse<ModelProviderResult> detail(@PathVariable String providerCode) {
        return CenterResponses.success(modelProviderAdminService.getByCode(providerCode));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "创建模型提供商")
    @PostMapping
    public ApiResponse<ModelProviderResult> create(@RequestBody ModelProviderUpsertRequest request) {
        return CenterResponses.success(modelProviderAdminService.create(request));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "更新模型提供商")
    @PostMapping("/update")
    public ApiResponse<ModelProviderResult> update(@RequestBody ModelProviderUpsertRequest request) {
        return CenterResponses.success(modelProviderAdminService.update(request.providerCode(), request));
    }
}
