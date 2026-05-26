package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.ListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Capability 管理接口。
 */
@RequestMapping("/api/center/capabilities")
public class CapabilityController {

    private final CapabilityCenterService service;

    public CapabilityController(CapabilityCenterService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ListResponse<CapabilityDtos.Result>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/{capabilityCode}")
    public ApiResponse<CapabilityDtos.Result> get(@PathVariable("capabilityCode") String capabilityCode) {
        return ApiResponse.success(service.get(capabilityCode));
    }

    @PostMapping
    public ApiResponse<CapabilityDtos.Result> create(@RequestBody CapabilityDtos.CreateRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{capabilityCode}")
    public ApiResponse<CapabilityDtos.Result> update(
            @PathVariable("capabilityCode") String capabilityCode,
            @RequestBody CapabilityDtos.UpdateRequest request
    ) {
        return ApiResponse.success(service.update(capabilityCode, request));
    }
}
