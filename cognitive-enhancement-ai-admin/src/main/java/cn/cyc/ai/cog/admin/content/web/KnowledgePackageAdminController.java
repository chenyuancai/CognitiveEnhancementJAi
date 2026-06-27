package cn.cyc.ai.cog.admin.content.web;

import cn.cyc.ai.cog.admin.content.dto.KnowledgePackageItemVO;
import cn.cyc.ai.cog.admin.content.dto.KnowledgePackageVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackage;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackageItem;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageItemSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackagePageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.service.KnowledgePackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "内容-知识包", description = "知识包与条目管理")
@RestController
@RequestMapping("/api/admin/content/knowledge-packages")
public class KnowledgePackageAdminController {

    private final KnowledgePackageService knowledgePackageService;

    public KnowledgePackageAdminController(KnowledgePackageService knowledgePackageService) {
        this.knowledgePackageService = knowledgePackageService;
    }

    @Operation(summary = "知识包分页")
    @RequirePermission("admin:content:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<KnowledgePackageVO>> page(@RequestBody KnowledgePackagePageQuery query) {
        return ApiResponse.success(knowledgePackageService.page(query).map(this::toVo));
    }

    @Operation(summary = "知识包详情")
    @RequirePermission("admin:content:update")
    @GetMapping("/{id}")
    public ApiResponse<KnowledgePackageVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(knowledgePackageService.detail(id)));
    }

    @Operation(summary = "知识包条目列表")
    @RequirePermission("admin:content:update")
    @GetMapping("/{id}/items")
    public ApiResponse<List<KnowledgePackageItemVO>> items(@PathVariable Long id) {
        return ApiResponse.success(knowledgePackageService.listItems(id).stream().map(this::toItemVo).toList());
    }

    @Operation(summary = "新增知识包")
    @RequirePermission("admin:content:create")
    @PostMapping
    public ApiResponse<KnowledgePackageVO> create(@Valid @RequestBody KnowledgePackageSaveRequest request) {
        return ApiResponse.success(toVo(knowledgePackageService.create(request)));
    }

    @Operation(summary = "编辑知识包")
    @RequirePermission("admin:content:update")
    @PostMapping("/update")
    public ApiResponse<KnowledgePackageVO> update(@Valid @RequestBody KnowledgePackageSaveRequest request) {
        return ApiResponse.success(toVo(knowledgePackageService.update(request.getId(), request)));
    }

    @Operation(summary = "新增知识包条目")
    @RequirePermission("admin:content:update")
    @PostMapping("/items")
    public ApiResponse<KnowledgePackageItemVO> addItem(@Valid @RequestBody KnowledgePackageItemSaveRequest request) {
        return ApiResponse.success(toItemVo(knowledgePackageService.addItem(request.getPackageId(), request)));
    }

    @Operation(summary = "删除知识包条目")
    @RequirePermission("admin:content:delete")
    @DeleteMapping("/{packageId}/items/{itemId}")
    public ApiResponse<Void> deleteItem(@PathVariable Long packageId, @PathVariable Long itemId) {
        knowledgePackageService.deleteItem(packageId, itemId);
        return ApiResponse.success(null);
    }

    private KnowledgePackageVO toVo(KnowledgePackage pkg) {
        KnowledgePackageVO vo = new KnowledgePackageVO();
        vo.setId(pkg.id());
        vo.setPackageName(pkg.packageName());
        vo.setDescription(pkg.description());
        vo.setStatus(pkg.status());
        return vo;
    }

    private KnowledgePackageItemVO toItemVo(KnowledgePackageItem item) {
        KnowledgePackageItemVO vo = new KnowledgePackageItemVO();
        vo.setId(item.id());
        vo.setPackageId(item.packageId());
        vo.setParentId(item.parentId());
        vo.setContentId(item.contentId());
        vo.setTitle(item.title());
        vo.setSortNo(item.sortNo());
        return vo;
    }
}
