package cn.cyc.ai.cog.app.tutoring.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppMistakeRecordVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringMistakePageQuery;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringMistakeService;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错题本接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-学习辅导", description = "学习辅导型 AI 对话")
@RestController
@RequestMapping("/api/app/tutoring")
public class AppTutoringMistakeController {

    /**
     * 错题本服务。
     */
    private final AppTutoringMistakeService mistakeService;

    /**
     * 创建错题本控制器。
     *
     * @param mistakeService 错题本服务
     */
    public AppTutoringMistakeController(AppTutoringMistakeService mistakeService) {
        this.mistakeService = mistakeService;
    }

    /**
     * 分页查询当前用户的错题记录。
     *
     * @param query 分页查询条件
     * @return 错题记录分页结果
     */
    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "分页查询错题记录")
    @PostMapping("/mistakes/page")
    public ApiResponse<PageResult<AppMistakeRecordVO>> page(@Valid @RequestBody AppTutoringMistakePageQuery query) {
        return ApiResponse.success(mistakeService.pageForCurrentUser(query));
    }
}
