package cn.cyc.ai.cog.admin.operation.web;

import cn.cyc.ai.cog.platform.operations.dto.BannerPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.BannerSaveRequest;
import cn.cyc.ai.cog.admin.operation.dto.BannerVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Banner;
import cn.cyc.ai.cog.platform.operations.service.BannerService;
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

@Tag(name = "运营-Banner", description = "运营位 Banner CRUD")
@RestController
@RequestMapping("/api/admin/operations/banners")
public class BannerAdminController {

    private final BannerService bannerService;

    public BannerAdminController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @Operation(summary = "分页查询 Banner")
    @RequirePermission("admin:banner:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<BannerVO>> page(@RequestBody BannerPageQuery query) {
        return ApiResponse.success(bannerService.page(query).map(this::toVo));
    }

    @Operation(summary = "Banner 详情")
    @RequirePermission("admin:banner:update")
    @GetMapping("/{id}")
    public ApiResponse<BannerVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(bannerService.detail(id)));
    }

    @Operation(summary = "新增 Banner")
    @RequirePermission("admin:banner:create")
    @PostMapping
    public ApiResponse<BannerVO> create(@Valid @RequestBody BannerSaveRequest request) {
        return ApiResponse.success(toVo(bannerService.create(request)));
    }

    @Operation(summary = "编辑 Banner")
    @RequirePermission("admin:banner:update")
    @PostMapping("/update")
    public ApiResponse<BannerVO> update(@Valid @RequestBody BannerSaveRequest request) {
        return ApiResponse.success(toVo(bannerService.update(request.getId(), request)));
    }

    @Operation(summary = "删除 Banner")
    @RequirePermission("admin:banner:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ApiResponse.success(null);
    }

    private BannerVO toVo(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.id());
        vo.setTitle(banner.title());
        vo.setImageUrl(banner.imageUrl());
        vo.setLinkUrl(banner.linkUrl());
        vo.setPosition(banner.position());
        vo.setSortNo(banner.sortNo());
        vo.setStatus(banner.status());
        vo.setStartTime(banner.startTime());
        vo.setEndTime(banner.endTime());
        return vo;
    }
}
