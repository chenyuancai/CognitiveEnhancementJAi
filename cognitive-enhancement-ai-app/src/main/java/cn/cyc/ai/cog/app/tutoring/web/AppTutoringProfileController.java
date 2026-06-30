package cn.cyc.ai.cog.app.tutoring.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学习画像接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-学习辅导", description = "学习辅导型 AI 对话")
@RestController
@RequestMapping("/api/app/tutoring")
public class AppTutoringProfileController {

    /**
     * 学习画像服务。
     */
    private final AppTutoringProfileService profileService;

    /**
     * 创建学习画像控制器。
     *
     * @param profileService 学习画像服务
     */
    public AppTutoringProfileController(AppTutoringProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * 查询当前用户的学习画像。
     *
     * @return 学习画像
     */
    /**
     * 执行画像。
     * @return 执行结果
     */
    @Operation(summary = "查询当前用户学习画像")
    @GetMapping("/profile")
    public ApiResponse<AppLearningProfile> profile() {
        return ApiResponse.success(profileService.loadForCurrentUser());
    }
}
