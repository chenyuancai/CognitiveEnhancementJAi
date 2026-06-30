package cn.cyc.ai.cog.admin.content.web;

import cn.cyc.ai.cog.admin.content.dto.ContentImportJobVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentImportJob;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobCreateRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobPageQuery;
import cn.cyc.ai.cog.platform.knowledge.service.ContentImportJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容ImportJob管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "内容-导入", description = "内容批量导入任务")
@RestController
@RequestMapping("/api/admin/content/import-jobs")
public class ContentImportJobAdminController {

    /** 内容ImportJob服务。 */
    private final ContentImportJobService contentImportJobService;

    /**
     * 创建内容ImportJob管理后台接口。
     *
     * @param contentImportJobService 内容ImportJob服务
     */
    public ContentImportJobAdminController(ContentImportJobService contentImportJobService) {
        this.contentImportJobService = contentImportJobService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "导入任务分页")
    @RequirePermission("admin:content:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<ContentImportJobVO>> page(@RequestBody ContentImportJobPageQuery query) {
        return ApiResponse.success(contentImportJobService.page(query).map(this::toVo));
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Operation(summary = "导入任务详情")
    @RequirePermission("admin:content:update")
    @GetMapping("/{id}")
    public ApiResponse<ContentImportJobVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(contentImportJobService.detail(id)));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "创建导入任务")
    @RequirePermission("admin:content:create")
    @PostMapping
    public ApiResponse<ContentImportJobVO> create(@Valid @RequestBody ContentImportJobCreateRequest request) {
        return ApiResponse.success(toVo(contentImportJobService.create(request)));
    }

    /**
     * 转换为Vo。
     *
     * @param job job
     * @return 转换结果
     */
    private ContentImportJobVO toVo(ContentImportJob job) {
        ContentImportJobVO vo = new ContentImportJobVO();
        vo.setId(job.id());
        vo.setTenantId(job.tenantId());
        vo.setFileName(job.fileName());
        vo.setFileUrl(job.fileUrl());
        vo.setStatus(job.status());
        vo.setTotalCount(job.totalCount());
        vo.setSuccessCount(job.successCount());
        vo.setFailCount(job.failCount());
        vo.setResultJson(job.resultJson());
        vo.setCreateBy(job.createBy());
        vo.setCreateTime(job.createTime());
        vo.setUpdateTime(job.updateTime());
        return vo;
    }
}
