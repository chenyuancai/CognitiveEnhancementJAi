package cn.cyc.ai.cog.admin.operation.web;

import cn.cyc.ai.cog.admin.operation.dto.AnnouncementVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementSaveRequest;
import cn.cyc.ai.cog.platform.operations.service.AnnouncementService;
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

@Tag(name = "运营-公告", description = "系统公告管理")
@RestController
@RequestMapping("/api/admin/operations/announcements")
public class AnnouncementAdminController {

    private final AnnouncementService announcementService;

    public AnnouncementAdminController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @Operation(summary = "公告分页")
    @RequirePermission("admin:banner:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<AnnouncementVO>> page(@RequestBody AnnouncementPageQuery query) {
        return ApiResponse.success(announcementService.page(query).map(this::toVo));
    }

    @Operation(summary = "公告详情")
    @RequirePermission("admin:banner:update")
    @GetMapping("/{id}")
    public ApiResponse<AnnouncementVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(announcementService.detail(id)));
    }

    @Operation(summary = "新增公告")
    @RequirePermission("admin:banner:create")
    @PostMapping
    public ApiResponse<AnnouncementVO> create(@Valid @RequestBody AnnouncementSaveRequest request) {
        return ApiResponse.success(toVo(announcementService.create(request)));
    }

    @Operation(summary = "编辑公告")
    @RequirePermission("admin:banner:update")
    @PostMapping("/update")
    public ApiResponse<AnnouncementVO> update(@Valid @RequestBody AnnouncementSaveRequest request) {
        return ApiResponse.success(toVo(announcementService.update(request.getId(), request)));
    }

    @Operation(summary = "删除公告")
    @RequirePermission("admin:banner:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ApiResponse.success(null);
    }

    private AnnouncementVO toVo(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(announcement.id());
        vo.setTitle(announcement.title());
        vo.setBody(announcement.body());
        vo.setStatus(announcement.status());
        vo.setPublishAt(announcement.publishAt());
        vo.setTargetLevelCodes(announcement.targetLevelCodes());
        vo.setTargetUserIds(announcement.targetUserIds());
        return vo;
    }
}
