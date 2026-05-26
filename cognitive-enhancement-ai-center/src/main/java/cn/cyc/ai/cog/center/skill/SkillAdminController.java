package cn.cyc.ai.cog.center.skill;

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
 * Skill 后台管理接口。
 *
 * @author cyc
 */
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
    @GetMapping
    public ApiResponse<DefinitionListResult<SkillResult>> listAll() {
        log.info("收到查询全部 Skill 定义请求");
        return CenterResponses.success(skillAdminService.listAll());
    }

    /**
     * 按编码查询 Skill 定义详情。
     *
     * @param code Skill 编码
     * @return Skill 定义详情
     */
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
    @PutMapping("/{code}")
    public ApiResponse<SkillResult> update(@PathVariable("code") String code, @RequestBody SkillUpsertRequest request) {
        log.info("收到更新 Skill 定义请求，code={}", code);
        return CenterResponses.success(skillAdminService.update(code, request));
    }
}
