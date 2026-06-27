package cn.cyc.ai.cog.runtime.feedback.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.feedback.domain.ExecutionFeedback;
import cn.cyc.ai.cog.runtime.feedback.dto.SubmitFeedbackRequest;
import cn.cyc.ai.cog.runtime.feedback.service.ExecutionFeedbackService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 执行反馈接口。
 *
 * @author cyc
 */
@Tag(name = "Runtime - 反馈", description = "执行结果反馈闭环")
@RestController
@RequestMapping("/api/runtime/feedback")
public class RuntimeFeedbackController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RuntimeFeedbackController.class);

    /**
     * 执行反馈服务。
     */
    private final ExecutionFeedbackService executionFeedbackService;

    /**
     * 构造 Runtime 执行反馈接口。
     *
     * @param executionFeedbackService 执行反馈服务
     */
    public RuntimeFeedbackController(ExecutionFeedbackService executionFeedbackService) {
        this.executionFeedbackService = executionFeedbackService;
    }

    /**
     * 提交执行反馈。
     *
     * @param request 提交反馈请求
     * @return 保存后的反馈记录
     */
    @Operation(summary = "提交执行反馈", description = "针对某次执行（traceId/sessionId）提交评分与纠正内容，形成反馈闭环。")
    @PostMapping
    public ApiResponse<ExecutionFeedback> submitFeedback(@RequestBody SubmitFeedbackRequest request) {
        log.info("收到执行反馈提交请求, traceId={}, sessionId={}", request.traceId(), request.sessionId());
        return RuntimeResponses.success(executionFeedbackService.submitFeedback(
                request.traceId(),
                request.sessionId(),
                request.rating(),
                request.originalAnswer(),
                request.correctedAnswer(),
                request.comment()));
    }

    /**
     * 查询执行反馈列表。
     *
     * @param traceId   链路 TraceId 筛选条件
     * @param sessionId 会话 ID 筛选条件
     * @return 反馈列表
     */
    @Operation(summary = "查询执行反馈列表", description = "按 traceId/sessionId 查询历史反馈记录。")
    @GetMapping
    public ApiResponse<RuntimeListResult<ExecutionFeedback>> listFeedback(
            @RequestParam(name = "traceId", required = false) String traceId,
            @RequestParam(name = "sessionId", required = false) String sessionId) {
        log.info("收到执行反馈查询请求, traceId={}, sessionId={}", traceId, sessionId);
        var feedbacks = executionFeedbackService.listFeedback(traceId, sessionId);
        return RuntimeResponses.success(new RuntimeListResult<>(feedbacks.size(), feedbacks));
    }
}
