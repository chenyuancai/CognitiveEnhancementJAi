package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.CenterResponses;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
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

import java.util.List;

/**
 * Prompt 模板后台管理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Center - Prompt", description = "Prompt 模板管理与发布：版本、灰度、下线")
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
     * Prompt 发布管理服务。
     */
    private final PromptReleaseService promptReleaseService;

    /**
     * 创建 Prompt 后台管理控制器。
     *
     * @param promptAdminService   Prompt 后台管理服务
     * @param promptReleaseService Prompt 发布管理服务
     */
    public PromptAdminController(PromptAdminService promptAdminService,
                                 PromptReleaseService promptReleaseService) {
        this.promptAdminService = promptAdminService;
        this.promptReleaseService = promptReleaseService;
    }

    /**
     * 查询全部 Prompt 模板。
     *
     * @return Prompt 模板列表
     */
    @Operation(summary = "分页查询 Prompt 列表", description = "支持 promptCode/scenarioCode/status 等条件分页查询 Prompt 模板。")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<PromptResult>> listAll(@RequestBody PromptPageQuery query) {
        log.info("收到查询全部 Prompt 模板请求");
        return CenterResponses.success(promptAdminService.listPage(query));
    }

    /**
     * 按编码查询 Prompt 模板详情。
     *
     * @param code Prompt 编码
     * @return Prompt 模板详情
     */
    @Operation(summary = "查询 Prompt 详情", description = "按 promptCode 返回模板内容与 Schema。")
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
    @Operation(summary = "创建 Prompt", description = "新建 Prompt 模板；promptCode 唯一，创建后不可修改。")
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
    @Operation(summary = "更新 Prompt", description = "按 promptCode 更新模板。")
    @PostMapping("/update")
    public ApiResponse<PromptResult> update(@RequestBody PromptUpsertRequest request) {
        log.info("收到更新 Prompt 模板请求，code={}", request.promptCode());
        return CenterResponses.success(promptAdminService.update(request.promptCode(), request));
    }

    /**
     * 列出 Prompt 全部版本。
     */
    @Operation(summary = "查询 Prompt 版本列表", description = "返回同一 promptCode 下的全部版本。")
    @GetMapping("/{code}/versions")
    public ApiResponse<List<PromptResult>> listVersions(@PathVariable("code") String code) {
        log.info("收到查询 Prompt 版本列表请求，code={}", code);
        return CenterResponses.success(promptReleaseService.listVersions(code));
    }

    /**
     * 创建草稿新版本。
     */
    @Operation(summary = "创建 Prompt 草稿", description = "基于现有模板创建草稿新版本。")
    @PostMapping("/drafts")
    public ApiResponse<PromptResult> createDraft(@RequestBody PromptDraftRequest request) {
        log.info("收到创建 Prompt 草稿请求，code={}", request.promptCode());
        return CenterResponses.success(promptReleaseService.createDraft(request.promptCode(), request));
    }

    /**
     * 发布指定版本。
     */
    @Operation(summary = "发布 Prompt 版本", description = "将指定版本设为已发布。")
    @PostMapping("/publish")
    public ApiResponse<PromptResult> publish(@RequestBody PromptPublishRequest request) {
        log.info("收到发布 Prompt 请求，code={}, version={}", request.promptCode(), request.version());
        return CenterResponses.success(promptReleaseService.publish(request.promptCode(), request));
    }

    /**
     * 下线指定版本。
     */
    @Operation(summary = "下线 Prompt 版本", description = "将指定版本标记为下线。")
    @PostMapping("/offline")
    public ApiResponse<PromptResult> offline(@RequestBody PromptOfflineRequest request) {
        log.info("收到下线 Prompt 请求，code={}, version={}", request.promptCode(), request.version());
        return CenterResponses.success(promptReleaseService.offline(request.promptCode(), request));
    }

    /**
     * 配置灰度规则。
     */
    @Operation(summary = "配置 Prompt 灰度", description = "设置灰度版本与流量规则。")
    @PostMapping("/gray")
    public ApiResponse<PromptReleasePointer> configureGray(@RequestBody PromptGrayRequest request) {
        log.info("收到配置 Prompt 灰度请求，code={}", request.promptCode());
        return CenterResponses.success(promptReleaseService.configureGray(request.promptCode(), request));
    }
}
