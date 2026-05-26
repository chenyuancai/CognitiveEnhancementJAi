package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.ListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Tool 管理接口。
 */
@RequestMapping("/api/center/tools")
public class ToolController {

    private final ToolCenterService service;

    public ToolController(ToolCenterService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ListResponse<ToolDtos.Result>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/{toolCode}")
    public ApiResponse<ToolDtos.Result> get(@PathVariable("toolCode") String toolCode) {
        return ApiResponse.success(service.get(toolCode));
    }

    @PostMapping
    public ApiResponse<ToolDtos.Result> create(@RequestBody ToolDtos.CreateRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{toolCode}")
    public ApiResponse<ToolDtos.Result> update(
            @PathVariable("toolCode") String toolCode,
            @RequestBody ToolDtos.UpdateRequest request
    ) {
        return ApiResponse.success(service.update(toolCode, request));
    }
}
