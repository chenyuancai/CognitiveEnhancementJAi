package cn.cyc.ai.cog.center.skill;

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
 * Skill 后台管理接口。
 *
 * @author cyc
 */
@Tag(name = "Center - Skill", description = "Skill 技能元数据管理：绑定 Tool、依赖与输出治理规则")
@RestController
@RequestMapping("/api/center/skills")
public class SkillAdminController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(SkillAdminController.class);

    /**
     * Skill 后台管理服务。
     */
    private final SkillAdminService skillAdminService;

    /**
     * 创建 Skill 后台管理控制器。
     *
     * @param skillAdminService Skill 后台管理服务
     */
    public SkillAdminController(SkillAdminService skillAdminService) {
        this.skillAdminService = skillAdminService;
    }

    /**
     * 查询全部 Skill 定义。
     *
     * @return Skill 定义列表
     */
    @Operation(summary = "分页查询 Skill 列表", description = "支持 skillCode/skillType/status 等条件分页查询 Skill 定义。")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<SkillResult>> listAll(@RequestBody SkillPageQuery query) {
        log.info("收到查询全部 Skill 定义请求");
        return CenterResponses.success(skillAdminService.listPage(query));
    }

    /**
     * 按编码查询 Skill 定义详情。
     *
     * @param code Skill 编码
     * @return Skill 定义详情
     */
    @Operation(summary = "查询 Skill 详情", description = "按 skillCode 返回技能定义。")
    @GetMapping("/{code}")
    public ApiResponse<SkillResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询 Skill 定义详情请求，code={}", code);
        return CenterResponses.success(skillAdminService.getByCode(code));
    }

    /**
     * 创建 Skill 定义。
     *
     * @param request Skill 写入请求
     * @return 创建后的 Skill 定义
     */
    @Operation(summary = "创建 Skill", description = "新建 Skill 定义；skillCode 唯一，创建后不可修改。")
    @PostMapping
    public ApiResponse<SkillResult> create(@RequestBody SkillUpsertRequest request) {
        log.info("收到创建 Skill 定义请求，skillCode={}", request.skillCode());
        return CenterResponses.success(skillAdminService.create(request));
    }

    /**
     * 更新 Skill 定义。
     *
     * @param code    Skill 编码
     * @param request Skill 写入请求
     * @return 更新后的 Skill 定义
     */
    @Operation(summary = "更新 Skill", description = "按 skillCode 更新技能定义。")
    @PostMapping("/update")
    public ApiResponse<SkillResult> update(@RequestBody SkillUpsertRequest request) {
        log.info("收到更新 Skill 定义请求，code={}", request.skillCode());
        return CenterResponses.success(skillAdminService.update(request.skillCode(), request));
    }
}
