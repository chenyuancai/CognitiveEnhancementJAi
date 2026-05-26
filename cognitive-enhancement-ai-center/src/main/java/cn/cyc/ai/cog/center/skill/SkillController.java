package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.ListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Skill 管理接口。
 */
@RequestMapping("/api/center/skills")
public class SkillController {

    private final SkillCenterService service;

    public SkillController(SkillCenterService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ListResponse<SkillDtos.Result>> list() {
        return ApiResponse.success(service.list());
    }

    @GetMapping("/{skillCode}")
    public ApiResponse<SkillDtos.Result> get(@PathVariable("skillCode") String skillCode) {
        return ApiResponse.success(service.get(skillCode));
    }

    @PostMapping
    public ApiResponse<SkillDtos.Result> create(@RequestBody SkillDtos.CreateRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{skillCode}")
    public ApiResponse<SkillDtos.Result> update(
            @PathVariable("skillCode") String skillCode,
            @RequestBody SkillDtos.UpdateRequest request
    ) {
        return ApiResponse.success(service.update(skillCode, request));
    }
}
