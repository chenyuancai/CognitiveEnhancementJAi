package cn.cyc.ai.cog.center.tool;

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
 * Tool 后台管理接口。
 *
 * @author cyc
 */
@Tag(name = "Center - Tool", description = "Tool 工具元数据管理：JAVA_LOCAL / HTTP / MCP 协议配置")
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
    @Operation(summary = "分页查询 Tool 列表", description = "支持 toolCode/protocolType/status 等条件分页查询 Tool 定义。")
    @PostMapping("/page")
    public ApiResponse<CenterPageResult<ToolResult>> listAll(@RequestBody ToolPageQuery query) {
        log.info("收到查询全部 Tool 定义请求");
        return CenterResponses.success(toolAdminService.listPage(query));
    }

    /**
     * 按编码查询 Tool 定义详情。
     *
     * @param code Tool 编码
     * @return Tool 定义详情
     */
    @Operation(summary = "查询 Tool 详情", description = "按 toolCode 返回 Tool 定义与 implRef。")
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
    @Operation(summary = "创建 Tool", description = "新建 Tool 定义；toolCode 唯一，创建后不可修改。")
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
    @Operation(summary = "更新 Tool", description = "按 toolCode 更新 Tool 定义。")
    @PostMapping("/update")
    public ApiResponse<ToolResult> update(@RequestBody ToolUpsertRequest request) {
        log.info("收到更新 Tool 定义请求，code={}", request.toolCode());
        return CenterResponses.success(toolAdminService.update(request.toolCode(), request));
    }
}
