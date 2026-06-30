package cn.cyc.ai.cog.app.tutoring.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringSessionPageQuery;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringSessionSummaryVO;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringSessionService;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端 AI 学习辅导会话接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-AI助手", description = "学习辅导型 AI 对话")
@RestController
@RequestMapping("/api/app/tutoring")
public class AppTutoringSessionController {

    /**
     * 学习辅导会话查询服务。
     */
    private final AppTutoringSessionService appTutoringSessionService;

    /**
     * 创建学习辅导会话控制器。
     *
     * @param appTutoringSessionService 学习辅导会话查询服务
     */
    public AppTutoringSessionController(AppTutoringSessionService appTutoringSessionService) {
        this.appTutoringSessionService = appTutoringSessionService;
    }

    /**
     * 分页查询当前用户的学习辅导会话列表。
     *
     * @param query 分页查询条件，可为空
     * @return 会话摘要分页结果
     */
    /**
     * 执行分页Sessions。
     * @return 执行结果
     */
    @Operation(summary = "学习辅导会话分页", description = "返回当前用户的 tutoring 会话列表，按更新时间倒序")
    @PostMapping("/sessions/page")
    public ApiResponse<PageResult<AppTutoringSessionSummaryVO>> pageSessions(
            @RequestBody(required = false) AppTutoringSessionPageQuery query) {
        return ApiResponse.success(appTutoringSessionService.pageSessions(query));
    }
}
