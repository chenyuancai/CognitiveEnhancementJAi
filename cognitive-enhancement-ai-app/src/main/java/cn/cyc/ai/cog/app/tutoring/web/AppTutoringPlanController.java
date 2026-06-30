package cn.cyc.ai.cog.app.tutoring.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningPlanVO;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringLearningPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学习计划接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-学习辅导", description = "学习辅导型 AI 对话")
@RestController
@RequestMapping("/api/app/tutoring")
public class AppTutoringPlanController {

    /**
     * 学习计划服务。
     */
    private final AppTutoringLearningPlanService learningPlanService;

    /**
     * 创建学习计划控制器。
     *
     * @param learningPlanService 学习计划服务
     */
    public AppTutoringPlanController(AppTutoringLearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    /**
     * 查询当前用户活跃的学习计划。
     *
     * @return 活跃学习计划
     */
    /**
     * 执行active计划。
     * @return 执行结果
     */
    @Operation(summary = "查询当前活跃学习计划")
    @GetMapping("/plan/active")
    public ApiResponse<AppLearningPlanVO> activePlan() {
        return ApiResponse.success(learningPlanService.findActiveForCurrentUser());
    }
}
