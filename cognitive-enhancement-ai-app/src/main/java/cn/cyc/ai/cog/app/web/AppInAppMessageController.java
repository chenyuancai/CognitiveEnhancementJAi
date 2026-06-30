package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.api.request.IdentifiedCommand;
import cn.cyc.ai.cog.app.dto.AppInAppMessageQuery;
import cn.cyc.ai.cog.app.dto.AppInAppMessageVO;
import cn.cyc.ai.cog.app.service.AppInAppMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端InC端消息接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-站内信", description = "C 端站内信查询与已读")
@RestController
@RequestMapping("/api/app/ops/in-app-messages")
public class AppInAppMessageController {

    /** C端InC端消息服务。 */
    private final AppInAppMessageService appInAppMessageService;

    /**
     * 创建C端InC端消息接口。
     *
     * @param appInAppMessageService C端InC端消息服务
     */
    public AppInAppMessageController(AppInAppMessageService appInAppMessageService) {
        this.appInAppMessageService = appInAppMessageService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "我的站内信")
    @PostMapping("/page")
    public ApiResponse<List<AppInAppMessageVO>> page(@RequestBody(required = false) AppInAppMessageQuery query) {
        Boolean read = query == null ? null : query.getRead();
        return ApiResponse.success(appInAppMessageService.list(read));
    }

    /**
     * 执行markRead。
     *
     * @param command command
     * @return 执行结果
     */
    @Operation(summary = "标记已读")
    @PostMapping("/read")
    public ApiResponse<AppInAppMessageVO> markRead(@Valid @RequestBody IdentifiedCommand command) {
        return ApiResponse.success(appInAppMessageService.markRead(command.id()));
    }
}
