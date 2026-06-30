package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.dto.AppAnnouncementVO;
import cn.cyc.ai.cog.app.dto.AppBannerQuery;
import cn.cyc.ai.cog.app.dto.AppBannerVO;
import cn.cyc.ai.cog.app.service.AppOpsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C 端运营投放接口（Banner / 公告）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-运营投放", description = "Banner 与公告只读查询")
@RestController
@RequestMapping("/api/app/ops")
public class AppOpsController {

    /** C端Ops服务。 */
    private final AppOpsService appOpsService;

    /**
     * 创建C端Ops接口。
     *
     * @param appOpsService C端Ops服务
     */
    public AppOpsController(AppOpsService appOpsService) {
        this.appOpsService = appOpsService;
    }

    /**
     * 执行banners。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "生效 Banner 列表", description = "按展示位与生效时间过滤，默认 HOME_TOP。")
    @PostMapping("/banners/page")
    public ApiResponse<List<AppBannerVO>> banners(@RequestBody(required = false) AppBannerQuery query) {
        String position = query == null ? null : query.getPosition();
        return ApiResponse.success(appOpsService.listActiveBanners(position));
    }

    /**
     * 执行announcements。
     * @return 执行结果
     */
    @Operation(summary = "已发布公告列表")
    @GetMapping("/announcements")
    public ApiResponse<List<AppAnnouncementVO>> announcements() {
        return ApiResponse.success(appOpsService.listPublishedAnnouncements());
    }
}
