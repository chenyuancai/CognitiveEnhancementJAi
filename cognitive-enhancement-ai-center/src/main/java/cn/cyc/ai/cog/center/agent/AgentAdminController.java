package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.support.CenterResponses;
import cn.cyc.ai.cog.center.support.DefinitionListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agent 后台管理接口。
 *
 * @author cyc
 */
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
    @GetMapping
    public ApiResponse<DefinitionListResult<AgentResult>> listAll() {
        log.info("收到查询全部 Agent 定义请求");
        return CenterResponses.success(agentAdminService.listAll());
    }

    /**
     * 按编码查询 Agent 定义详情。
     *
     * @param code Agent 编码
     * @return Agent 定义详情
     */
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
    @PutMapping("/{code}")
    public ApiResponse<AgentResult> update(@PathVariable("code") String code, @RequestBody AgentUpsertRequest request) {
        log.info("收到更新 Agent 定义请求，code={}", code);
        return CenterResponses.success(agentAdminService.update(code, request));
    }
}
