package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.dto.AppLearningInvokeRequest;
import cn.cyc.ai.cog.app.dto.AppLearningModesVO;
import cn.cyc.ai.cog.app.service.AppLearningService;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端学习链路接口：评分 / 带学 / 问答。
 */
@Tag(name = "App-学习链路", description = "按会员权益调用 AI 能力（委托 Runtime）")
@RestController
@RequestMapping("/api/app/learning")
public class AppLearningController {

    /** C 端学习服务 */
    private final AppLearningService appLearningService;

    /**
     * @param appLearningService C 端学习服务
     */
    public AppLearningController(AppLearningService appLearningService) {
        this.appLearningService = appLearningService;
    }

    @Operation(summary = "可用学习模式")
    @GetMapping("/modes")
    public ApiResponse<AppLearningModesVO> modes() {
        return ApiResponse.success(appLearningService.listModes());
    }

    @Operation(summary = "调用学习 AI 能力", description = "mode=SCORING|TUTORING|QA")
    @PostMapping("/invoke")
    public ApiResponse<CapabilityExecuteResponse> invoke(@Valid @RequestBody AppLearningInvokeRequest request) {
        return ApiResponse.success(appLearningService.invoke(request));
    }
}
