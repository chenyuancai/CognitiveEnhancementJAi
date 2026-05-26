package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.ListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Agent 管理接口。
 */
@RequestMapping("/api/center/agents")
public class AgentController {

    private final AgentCenterService service;

    public AgentController(AgentCenterService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ListResponse<AgentDtos.Result>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/{agentCode}")
    public ApiResponse<AgentDtos.Result> get(@PathVariable("agentCode") String agentCode) {
        return ApiResponse.success(service.get(agentCode));
    }

    @PostMapping
    public ApiResponse<AgentDtos.Result> create(@RequestBody AgentDtos.CreateRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{agentCode}")
    public ApiResponse<AgentDtos.Result> update(
            @PathVariable("agentCode") String agentCode,
            @RequestBody AgentDtos.UpdateRequest request
    ) {
        return ApiResponse.success(service.update(agentCode, request));
    }
}
