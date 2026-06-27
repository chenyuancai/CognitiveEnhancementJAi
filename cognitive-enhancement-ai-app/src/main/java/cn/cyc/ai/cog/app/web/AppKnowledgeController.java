package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.dto.AppContentDetailVO;
import cn.cyc.ai.cog.app.dto.AppContentSummaryVO;
import cn.cyc.ai.cog.app.dto.AppKnowledgePackageTreeVO;
import cn.cyc.ai.cog.app.service.AppKnowledgeService;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentPageQuery;
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
 * C 端知识内容只读接口。
 */
@Tag(name = "App-知识内容", description = "已发布内容与知识包目录（按会员等级过滤）")
@RestController
@RequestMapping("/api/app/knowledge")
public class AppKnowledgeController {

    /** C 端知识服务 */
    private final AppKnowledgeService appKnowledgeService;

    /**
     * @param appKnowledgeService C 端知识服务
     */
    public AppKnowledgeController(AppKnowledgeService appKnowledgeService) {
        this.appKnowledgeService = appKnowledgeService;
    }

    @Operation(summary = "已发布内容分页")
    @PostMapping("/contents/page")
    public ApiResponse<PageResult<AppContentSummaryVO>> pageContents(@RequestBody ContentPageQuery query) {
        return ApiResponse.success(appKnowledgeService.pagePublishedContents(query));
    }

    @Operation(summary = "已发布内容详情")
    @GetMapping("/contents/{id}")
    public ApiResponse<AppContentDetailVO> contentDetail(@PathVariable Long id) {
        return ApiResponse.success(appKnowledgeService.getPublishedContent(id));
    }

    @Operation(summary = "已启用知识包列表")
    @GetMapping("/packages")
    public ApiResponse<List<AppKnowledgePackageTreeVO>> listPackages() {
        return ApiResponse.success(appKnowledgeService.listEnabledPackages());
    }

    @Operation(summary = "知识包目录树")
    @GetMapping("/packages/{id}/tree")
    public ApiResponse<AppKnowledgePackageTreeVO> packageTree(@PathVariable Long id) {
        return ApiResponse.success(appKnowledgeService.getPackageTree(id));
    }
}
