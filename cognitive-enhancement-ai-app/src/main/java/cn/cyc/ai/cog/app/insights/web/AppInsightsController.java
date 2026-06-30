package cn.cyc.ai.cog.app.insights.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.insights.service.AppInsightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端学习画像 BFF：聚合辅导、练习与复习数据生成概览视图。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Tag(name = "App-学习画像", description = "学习画像概览 BFF")
@RestController
@RequestMapping("/api/app/insights")
public class AppInsightsController {

    private final AppInsightsService insightsService;

    public AppInsightsController(AppInsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @Operation(summary = "学习画像概览")
    @GetMapping("/overview")
    public ApiResponse<?> overview() {
        return ApiResponse.success(insightsService.buildOverview());
    }
}
