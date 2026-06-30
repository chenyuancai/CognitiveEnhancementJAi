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

/**
 * FeatureSwitch管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "系统-Feature开关", description = "灰度与功能开关")
@RestController
@RequestMapping("/api/admin/system/features")
public class FeatureSwitchAdminController {

    /** featureSwitch服务。 */
    private final FeatureSwitchService featureSwitchService;

    /**
     * 创建FeatureSwitch管理后台接口。
     *
     * @param featureSwitchService featureSwitch服务
     */
    public FeatureSwitchAdminController(FeatureSwitchService featureSwitchService) {
        this.featureSwitchService = featureSwitchService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "Feature 分页")
    @RequirePermission("admin:dict:read")
    @PostMapping("/page")
    public ApiResponse<PageResult<FeatureSwitchVO>> page(@RequestBody FeatureSwitchPageQuery query) {
        PageResult<FeatureSwitch> page = featureSwitchService.page(query);
        return ApiResponse.success(page.map(this::toVo));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新增 Feature")
    @RequirePermission("admin:dict:update")
    @PostMapping
    public ApiResponse<FeatureSwitchVO> create(@Valid @RequestBody FeatureSwitchSaveRequest request) {
        return ApiResponse.success(toVo(featureSwitchService.save(null, request)));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑 Feature")
    @RequirePermission("admin:dict:update")
    @PostMapping("/update")
    public ApiResponse<FeatureSwitchVO> update(@Valid @RequestBody FeatureSwitchSaveRequest request) {
        return ApiResponse.success(toVo(featureSwitchService.save(request.getId(), request)));
    }

    /**
     * 转换为Vo。
     *
     * @param featureSwitch featureSwitch
     * @return 转换结果
     */
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
