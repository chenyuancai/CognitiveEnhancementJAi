package cn.cyc.ai.cog.app.review.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.contract.AppPageQuery;
import cn.cyc.ai.cog.app.review.service.AppReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端复习接口：待复习列表、错题本与最近练习分页查询。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Tag(name = "App-复习", description = "待复习、错题本、最近练习")
@RestController
@RequestMapping("/api/app/review")
public class AppReviewController {

    private final AppReviewService reviewService;

    public AppReviewController(AppReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "待复习分页")
    @PostMapping("/pending/page")
    public ApiResponse<?> pendingPage(@RequestBody(required = false) AppPageQuery query) {
        return ApiResponse.success(reviewService.pagePending(query));
    }

    @Operation(summary = "错题本分页")
    @PostMapping("/error-book/page")
    public ApiResponse<?> errorBookPage(@RequestBody(required = false) AppPageQuery query) {
        return ApiResponse.success(reviewService.pageErrorBook(query));
    }

    @Operation(summary = "最近练习分页")
    @PostMapping("/recent-sessions/page")
    public ApiResponse<?> recentSessionsPage(@RequestBody(required = false) AppPageQuery query) {
        return ApiResponse.success(reviewService.pageRecentSessions(query));
    }
}
