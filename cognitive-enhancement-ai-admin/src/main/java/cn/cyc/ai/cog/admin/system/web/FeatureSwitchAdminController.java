package cn.cyc.ai.cog.admin.system.web;

import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.admin.system.dto.FeatureSwitchVO;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.FeatureSwitch;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchPageQuery;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchSaveRequest;
import cn.cyc.ai.cog.platform.system.service.FeatureSwitchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统-Feature开关", description = "灰度与功能开关")
@RestController
@RequestMapping("/api/admin/system/features")
public class FeatureSwitchAdminController {

    private final FeatureSwitchService featureSwitchService;

    public FeatureSwitchAdminController(FeatureSwitchService featureSwitchService) {
        this.featureSwitchService = featureSwitchService;
    }

    @Operation(summary = "Feature 分页")
    @RequirePermission("admin:dict:read")
    @PostMapping("/page")
    public ApiResponse<PageResult<FeatureSwitchVO>> page(@RequestBody FeatureSwitchPageQuery query) {
        PageResult<FeatureSwitch> page = featureSwitchService.page(query);
        return ApiResponse.success(page.map(this::toVo));
    }

    @Operation(summary = "新增 Feature")
    @RequirePermission("admin:dict:update")
    @PostMapping
    public ApiResponse<FeatureSwitchVO> create(@Valid @RequestBody FeatureSwitchSaveRequest request) {
        return ApiResponse.success(toVo(featureSwitchService.save(null, request)));
    }

    @Operation(summary = "编辑 Feature")
    @RequirePermission("admin:dict:update")
    @PostMapping("/update")
    public ApiResponse<FeatureSwitchVO> update(@Valid @RequestBody FeatureSwitchSaveRequest request) {
        return ApiResponse.success(toVo(featureSwitchService.save(request.getId(), request)));
    }

    private FeatureSwitchVO toVo(FeatureSwitch featureSwitch) {
        FeatureSwitchVO vo = new FeatureSwitchVO();
        vo.setId(featureSwitch.id());
        vo.setFeatureKey(featureSwitch.featureKey());
        vo.setFeatureName(featureSwitch.featureName());
        vo.setSegment(featureSwitch.segment());
        vo.setEnabled(featureSwitch.enabled());
        vo.setGrayRuleJson(featureSwitch.grayRuleJson());
        return vo;
    }
}
