package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.CenterResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agent 后台管理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Center - Agent", description = "Agent 元数据管理：定义角色、目标、绑定模型与技能")
@RestController
@RequestMapping("/api/center/agents")
public class AgentAdminController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(AgentAdminController.class);

    /**
     * Agent 后台管理服务。
     */
    private final AgentAdminService agentAdminService;

    /**
     * 创建 Agent 后台管理控制器。
     *
     * @param agentAdminService Agent 后台管理服务
     */
    public AgentAdminController(AgentAdminService agentAdminService) {
        this.agentAdminService = agentAdminService;
    }

    /**
     * 查询全部 Agent 定义。
     *
     * @return Agent 定义列表
     */
    @Operation(summary = "分页查询 Agent 列表", description = "支持 agentCode/agentName/status 等条件分页查询 Agent 定义。")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<AgentResult>> listAll(@RequestBody AgentPageQuery query) {
        log.info("收到查询全部 Agent 定义请求");
        return CenterResponses.success(agentAdminService.listPage(query));
    }

    /**
     * 按编码查询 Agent 定义详情。
     *
     * @param code Agent 编码
     * @return Agent 定义详情
     */
    @Operation(summary = "查询 Agent 详情", description = "按 agentCode 返回完整 Agent 定义。")
    @GetMapping("/{code}")
    public ApiResponse<AgentResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询 Agent 定义详情请求，code={}", code);
        return CenterResponses.success(agentAdminService.getByCode(code));
    }

    /**
     * 创建 Agent 定义。
     *
     * @param request Agent 写入请求
     * @return 创建后的 Agent 定义
     */
    @Operation(summary = "创建 Agent", description = "新建 Agent 定义；agentCode 唯一，创建后不可修改。")
    @PostMapping
    public ApiResponse<AgentResult> create(@RequestBody AgentUpsertRequest request) {
        log.info("收到创建 Agent 定义请求，agentCode={}", request.agentCode());
        return CenterResponses.success(agentAdminService.create(request));
    }

    /**
     * 更新 Agent 定义。
     *
     * @param code    Agent 编码
     * @param request Agent 写入请求
     * @return 更新后的 Agent 定义
     */
    @Operation(summary = "更新 Agent", description = "按 agentCode 更新 Agent 定义。")
    @PostMapping("/update")
    public ApiResponse<AgentResult> update(@RequestBody AgentUpsertRequest request) {
        log.info("收到更新 Agent 定义请求，code={}", request.agentCode());
        return CenterResponses.success(agentAdminService.update(request.agentCode(), request));
    }
}
