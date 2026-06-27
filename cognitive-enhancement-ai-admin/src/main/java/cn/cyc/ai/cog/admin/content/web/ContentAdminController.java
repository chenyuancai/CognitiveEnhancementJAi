package cn.cyc.ai.cog.admin.content.web;

import cn.cyc.ai.cog.admin.content.dto.ContentTagVO;
import cn.cyc.ai.cog.admin.content.dto.ContentVO;
import cn.cyc.ai.cog.admin.content.dto.ContentVersionVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;
import cn.cyc.ai.cog.api.request.IdentifiedCommand;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentAuditRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentRollbackRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagBindRequest;
import cn.cyc.ai.cog.platform.knowledge.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内容管理接口（内容-内容列表/审核）。
 *
 * @author cyc
 */
@Tag(name = "内容-内容管理", description = "内容草稿、列表、审核、上下线")
@RestController
@RequestMapping("/api/admin/content/contents")
public class ContentAdminController {

    private final ContentService contentService;

    public ContentAdminController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Operation(summary = "分页查询内容", description = "支持 keyword/type/status 过滤。需要 admin:content:update 权限点。")
    @RequirePermission("admin:content:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<ContentVO>> page(@RequestBody ContentPageQuery query) {
        return ApiResponse.success(contentService.page(query).map(this::toVo));
    }

    @Operation(summary = "内容详情")
    @RequirePermission("admin:content:update")
    @GetMapping("/{id}")
    public ApiResponse<ContentVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(contentService.detail(id)));
    }

    @Operation(summary = "新增内容", description = "创建后进入待审核。")
    @RequirePermission("admin:content:update")
    @PostMapping
    public ApiResponse<ContentVO> create(@Valid @RequestBody ContentSaveRequest request) {
        return ApiResponse.success(toVo(contentService.create(request)));
    }

    @Operation(summary = "编辑内容")
    @RequirePermission("admin:content:update")
    @PostMapping("/update")
    public ApiResponse<ContentVO> update(@Valid @RequestBody ContentSaveRequest request) {
        return ApiResponse.success(toVo(contentService.update(request.getId(), request)));
    }

    @Operation(summary = "审核内容", description = "通过则发布，否则驳回。")
    @RequirePermission("admin:content:audit")
    @PostMapping("/audit")
    public ApiResponse<ContentVO> audit(@Valid @RequestBody ContentAuditRequest request) {
        return ApiResponse.success(toVo(contentService.audit(request.getId(), request)));
    }

    @Operation(summary = "内容关联标签")
    @RequirePermission("admin:content:update")
    @GetMapping("/{id}/tags")
    public ApiResponse<List<ContentTagVO>> tags(@PathVariable Long id) {
        return ApiResponse.success(contentService.listTags(id).stream().map(this::toTagVo).toList());
    }

    @Operation(summary = "绑定内容标签")
    @RequirePermission("admin:content:update")
    @PostMapping("/tags")
    public ApiResponse<List<ContentTagVO>> bindTags(@RequestBody ContentTagBindRequest request) {
        return ApiResponse.success(contentService.bindTags(request.getContentId(), request).stream().map(this::toTagVo).toList());
    }

    @Operation(summary = "下线内容", description = "仅已发布内容可下线。")
    @RequirePermission("admin:content:audit")
    @PostMapping("/offline")
    public ApiResponse<ContentVO> offline(@Valid @RequestBody IdentifiedCommand command) {
        return ApiResponse.success(toVo(contentService.offline(command.id())));
    }

    @Operation(summary = "内容版本历史")
    @RequirePermission("admin:content:update")
    @GetMapping("/{id}/versions")
    public ApiResponse<List<ContentVersionVO>> versions(@PathVariable Long id) {
        return ApiResponse.success(contentService.listVersions(id).stream().map(this::toVersionVo).toList());
    }

    @Operation(summary = "回滚到历史版本", description = "将历史版本内容回填为草稿，需重新走审核发布。")
    @RequirePermission("admin:content:update")
    @PostMapping("/rollback")
    public ApiResponse<ContentVO> rollback(@Valid @RequestBody ContentRollbackRequest request) {
        return ApiResponse.success(toVo(contentService.rollback(request.getId(), request)));
    }

    private ContentVO toVo(Content content) {
        ContentVO vo = new ContentVO();
        vo.setId(content.id());
        vo.setTitle(content.title());
        vo.setContentType(content.contentType());
        vo.setAuthor(content.author());
        vo.setStatus(content.status());
        vo.setSummary(content.summary());
        vo.setBody(content.body());
        vo.setAuditRemark(content.auditRemark());
        vo.setMinLevelCode(content.minLevelCode());
        vo.setCurrentVersion(content.currentVersion());
        vo.setPublishedAt(content.publishedAt());
        return vo;
    }

    private ContentVersionVO toVersionVo(ContentVersion version) {
        ContentVersionVO vo = new ContentVersionVO();
        vo.setVersionNo(version.versionNo());
        vo.setTitle(version.title());
        vo.setMinLevelCode(version.minLevelCode());
        vo.setOperatorId(version.operatorId());
        vo.setCreateTime(version.createTime());
        return vo;
    }

    private ContentTagVO toTagVo(ContentTag tag) {
        ContentTagVO vo = new ContentTagVO();
        vo.setId(tag.id());
        vo.setTagName(tag.tagName());
        vo.setTagColor(tag.tagColor());
        return vo;
    }
}
