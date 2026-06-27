package cn.cyc.ai.cog.admin.content.web;

import cn.cyc.ai.cog.admin.content.dto.ContentTagVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.service.ContentTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "内容-标签", description = "内容标签 CRUD")
@RestController
@RequestMapping("/api/admin/content/tags")
public class ContentTagAdminController {

    private final ContentTagService contentTagService;

    public ContentTagAdminController(ContentTagService contentTagService) {
        this.contentTagService = contentTagService;
    }

    @Operation(summary = "标签分页")
    @RequirePermission("admin:content:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<ContentTagVO>> page(@RequestBody ContentTagPageQuery query) {
        return ApiResponse.success(contentTagService.page(query).map(this::toVo));
    }

    @Operation(summary = "新增标签")
    @RequirePermission("admin:content:create")
    @PostMapping
    public ApiResponse<ContentTagVO> create(@Valid @RequestBody ContentTagSaveRequest request) {
        return ApiResponse.success(toVo(contentTagService.create(request)));
    }

    @Operation(summary = "编辑标签")
    @RequirePermission("admin:content:update")
    @PostMapping("/update")
    public ApiResponse<ContentTagVO> update(@Valid @RequestBody ContentTagSaveRequest request) {
        return ApiResponse.success(toVo(contentTagService.update(request.getId(), request)));
    }

    @Operation(summary = "删除标签")
    @RequirePermission("admin:content:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        contentTagService.delete(id);
        return ApiResponse.success(null);
    }

    private ContentTagVO toVo(ContentTag tag) {
        ContentTagVO vo = new ContentTagVO();
        vo.setId(tag.id());
        vo.setTagName(tag.tagName());
        vo.setTagColor(tag.tagColor());
        return vo;
    }
}
