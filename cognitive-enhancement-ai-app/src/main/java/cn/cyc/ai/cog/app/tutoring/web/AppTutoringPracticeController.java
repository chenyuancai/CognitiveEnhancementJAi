package cn.cyc.ai.cog.app.tutoring.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppPracticeRecommendationVO;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringPracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 练习推荐接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-学习辅导", description = "学习辅导型 AI 对话")
@RestController
@RequestMapping("/api/app/tutoring")
public class AppTutoringPracticeController {

    /**
     * 练习推荐服务。
     */
    private final AppTutoringPracticeService practiceService;

    /**
     * 创建练习推荐控制器。
     *
     * @param practiceService 练习推荐服务
     */
    public AppTutoringPracticeController(AppTutoringPracticeService practiceService) {
        this.practiceService = practiceService;
    }

    /**
     * 查询指定会话下待完成的练习推荐。
     *
     * @param sessionId 会话 ID
     * @return 待完成练习列表
     */
    /**
     * 执行pending。
     *
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    @Operation(summary = "查询会话待完成练习")
    @GetMapping("/practice/pending/{sessionId}")
    public ApiResponse<List<AppPracticeRecommendationVO>> pending(@PathVariable String sessionId) {
        return ApiResponse.success(practiceService.listPendingForSession(sessionId));
    }
}
