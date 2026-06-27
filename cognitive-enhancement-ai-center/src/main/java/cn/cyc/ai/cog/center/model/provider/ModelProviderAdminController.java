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
 */
@Tag(name = "Center - ModelProvider", description = "模型提供商元数据管理")
@RestController
@RequestMapping("/api/center/model-providers")
public class ModelProviderAdminController {

    private final ModelProviderAdminService modelProviderAdminService;

    public ModelProviderAdminController(ModelProviderAdminService modelProviderAdminService) {
        this.modelProviderAdminService = modelProviderAdminService;
    }

    @Operation(summary = "分页查询模型提供商")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<ModelProviderResult>> page(@RequestBody ModelProviderPageQuery query) {
        return CenterResponses.success(modelProviderAdminService.listPage(query));
    }

    @Operation(summary = "查询全部启用的模型提供商", description = "供模型表单下拉选择")
    @GetMapping("/all")
    public ApiResponse<List<ModelProviderResult>> allEnabled() {
        return CenterResponses.success(modelProviderAdminService.listAllEnabled());
    }

    @Operation(summary = "查询模型提供商详情")
    @GetMapping("/{providerCode}")
    public ApiResponse<ModelProviderResult> detail(@PathVariable String providerCode) {
        return CenterResponses.success(modelProviderAdminService.getByCode(providerCode));
    }

    @Operation(summary = "创建模型提供商")
    @PostMapping
    public ApiResponse<ModelProviderResult> create(@RequestBody ModelProviderUpsertRequest request) {
        return CenterResponses.success(modelProviderAdminService.create(request));
    }

    @Operation(summary = "更新模型提供商")
    @PostMapping("/update")
    public ApiResponse<ModelProviderResult> update(@RequestBody ModelProviderUpsertRequest request) {
        return CenterResponses.success(modelProviderAdminService.update(request.providerCode(), request));
    }
}
