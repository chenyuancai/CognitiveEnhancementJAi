package cn.cyc.ai.cog.center.tool;

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
 * Tool 后台管理接口。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/center/tools")
public class ToolAdminController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ToolAdminController.class);

    /**
     * Tool 后台管理服务。
     */
    private final ToolAdminService toolAdminService;

    /**
     * 创建 Tool 后台管理控制器。
     *
     * @param toolAdminService Tool 后台管理服务
     */
    public ToolAdminController(ToolAdminService toolAdminService) {
        this.toolAdminService = toolAdminService;
    }

    /**
     * 查询全部 Tool 定义。
     *
     * @return Tool 定义列表
     */
    @GetMapping
    public ApiResponse<DefinitionListResult<ToolResult>> listAll() {
        log.info("收到查询全部 Tool 定义请求");
        return CenterResponses.success(toolAdminService.listAll());
    }

    /**
     * 按编码查询 Tool 定义详情。
     *
     * @param code Tool 编码
     * @return Tool 定义详情
     */
    @GetMapping("/{code}")
    public ApiResponse<ToolResult> getByCode(@PathVariable("code") String code) {
        log.info("收到查询 Tool 定义详情请求，code={}", code);
        return CenterResponses.success(toolAdminService.getByCode(code));
    }

    /**
     * 创建 Tool 定义。
     *
     * @param request Tool 写入请求
     * @return 创建后的 Tool 定义
     */
    @PostMapping
    public ApiResponse<ToolResult> create(@RequestBody ToolUpsertRequest request) {
        log.info("收到创建 Tool 定义请求，toolCode={}", request.toolCode());
        return CenterResponses.success(toolAdminService.create(request));
    }

    /**
     * 更新 Tool 定义。
     *
     * @param code    Tool 编码
     * @param request Tool 写入请求
     * @return 更新后的 Tool 定义
     */
    @PutMapping("/{code}")
    public ApiResponse<ToolResult> update(@PathVariable("code") String code, @RequestBody ToolUpsertRequest request) {
        log.info("收到更新 Tool 定义请求，code={}", code);
        return CenterResponses.success(toolAdminService.update(code, request));
    }
}
