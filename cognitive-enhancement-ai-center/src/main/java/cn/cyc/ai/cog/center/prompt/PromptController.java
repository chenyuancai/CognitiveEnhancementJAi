package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.ListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Prompt 管理接口。
 */
@RequestMapping("/api/center/prompts")
public class PromptController {

    private final PromptCenterService service;

    public PromptController(PromptCenterService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ListResponse<PromptDtos.Result>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/{promptCode}")
    public ApiResponse<PromptDtos.Result> get(@PathVariable("promptCode") String promptCode) {
        return ApiResponse.success(service.get(promptCode));
    }

    @PostMapping
    public ApiResponse<PromptDtos.Result> create(@RequestBody PromptDtos.CreateRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{promptCode}")
    public ApiResponse<PromptDtos.Result> update(
            @PathVariable("promptCode") String promptCode,
            @RequestBody PromptDtos.UpdateRequest request
    ) {
        return ApiResponse.success(service.update(promptCode, request));
    }
}
