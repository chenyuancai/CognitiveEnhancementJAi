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

/**
 * 内容标签管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "内容-标签", description = "内容标签 CRUD")
@RestController
@RequestMapping("/api/admin/content/tags")
public class ContentTagAdminController {

    /** 内容标签服务。 */
    private final ContentTagService contentTagService;

    /**
     * 创建内容标签管理后台接口。
     *
     * @param contentTagService 内容标签服务
     */
    public ContentTagAdminController(ContentTagService contentTagService) {
        this.contentTagService = contentTagService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "标签分页")
    @RequirePermission("admin:content:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<ContentTagVO>> page(@RequestBody ContentTagPageQuery query) {
        return ApiResponse.success(contentTagService.page(query).map(this::toVo));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新增标签")
    @RequirePermission("admin:content:create")
    @PostMapping
    public ApiResponse<ContentTagVO> create(@Valid @RequestBody ContentTagSaveRequest request) {
        return ApiResponse.success(toVo(contentTagService.create(request)));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑标签")
    @RequirePermission("admin:content:update")
    @PostMapping("/update")
    public ApiResponse<ContentTagVO> update(@Valid @RequestBody ContentTagSaveRequest request) {
        return ApiResponse.success(toVo(contentTagService.update(request.getId(), request)));
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    @Operation(summary = "删除标签")
    @RequirePermission("admin:content:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        contentTagService.delete(id);
        return ApiResponse.success(null);
    }

    /**
     * 转换为Vo。
     *
     * @param tag 标签
     * @return 转换结果
     */
    private ContentTagVO toVo(ContentTag tag) {
        ContentTagVO vo = new ContentTagVO();
        vo.setId(tag.id());
        vo.setTagName(tag.tagName());
        vo.setTagColor(tag.tagColor());
        return vo;
    }
}
