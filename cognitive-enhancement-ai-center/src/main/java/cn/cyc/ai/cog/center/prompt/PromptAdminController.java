package cn.cyc.ai.cog.center.prompt;

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
 * Prompt 模板后台管理接口。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/center/prompts")
public class PromptAdminController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PromptAdminController.class);

    /**
     * Prompt 后台管理服务。
     */
    private final PromptAdminService promptAdminService;

    /**
     * 创建 Prompt 后台管理控制器。
     *
     * @param promptAdminService Prompt 后台管理服务
     */
    public PromptAdminController(PromptAdminService promptAdminService) {
        this.promptAdminService = promptAdminService;
    }

    /**
     * 查询全部 Prompt 模板。
     *
     * @return Prompt 模板列表
     */
    @GetMapping
    public ApiResponse<DefinitionListResult<PromptResult>> listAll() {
        log.info("收到查询全部 Prompt 模板请求");
        return CenterResponses.success(promptAdminService.listAll());
    }

    /**
     * 按编码查询 Prompt 模板详情。
     *
     * @param code Prompt 编码
     * @return Prompt 模板详情
     */
    @GetMapping("/{code}")
    public ApiResponse<PromptResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询 Prompt 模板详情请求，code={}", code);
        return CenterResponses.success(promptAdminService.getByCode(code));
    }

    /**
     * 创建 Prompt 模板。
     *
     * @param request Prompt 写入请求
     * @return 创建后的 Prompt 模板
     */
    @PostMapping
    public ApiResponse<PromptResult> create(@RequestBody PromptUpsertRequest request) {
        log.info("收到创建 Prompt 模板请求，promptCode={}", request.promptCode());
        return CenterResponses.success(promptAdminService.create(request));
    }

    /**
     * 更新 Prompt 模板。
     *
     * @param code    Prompt 编码
     * @param request Prompt 写入请求
     * @return 更新后的 Prompt 模板
     */
    @PutMapping("/{code}")
    public ApiResponse<PromptResult> update(@PathVariable("code") String code, @RequestBody PromptUpsertRequest request) {
        log.info("收到更新 Prompt 模板请求，code={}", code);
        return CenterResponses.success(promptAdminService.update(code, request));
    }
}
