package cn.cyc.ai.cog.app.today.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.today.service.AppTodayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端今日页 BFF：聚合复习、练习推荐与快捷入口。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Tag(name = "App-今日", description = "今日页 BFF 聚合")
@RestController
@RequestMapping("/api/app/today")
public class AppTodayController {

    private final AppTodayService todayService;

    public AppTodayController(AppTodayService todayService) {
        this.todayService = todayService;
    }

    @Operation(summary = "今日页聚合数据")
    @GetMapping
    public ApiResponse<?> today() {
        return ApiResponse.success(todayService.buildToday());
    }
}
