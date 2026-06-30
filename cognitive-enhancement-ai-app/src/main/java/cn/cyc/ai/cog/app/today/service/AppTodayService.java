package cn.cyc.ai.cog.app.today.service;

import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.app.importtask.service.AppImportTaskService;
import cn.cyc.ai.cog.app.review.service.AppReviewService;
import cn.cyc.ai.cog.app.service.AppMeService;
import cn.cyc.ai.cog.app.service.AppOpsService;
import cn.cyc.ai.cog.app.today.assembler.AppTodayVoAssembler;
import cn.cyc.ai.cog.app.today.dto.AppTodayVO;
import cn.cyc.ai.cog.app.today.support.AppTodayRecommendationBuilder;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.practice.spi.PracticePersistencePort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 今日页 BFF 编排服务。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppTodayService {

    private final AppMeService appMeService;
    private final AppReviewService reviewService;
    private final AppOpsService appOpsService;
    private final PracticePersistencePort practicePersistence;
    private final AppImportTaskService importTaskService;
    private final AppTodayVoAssembler todayVoAssembler;
    private final AppTodayRecommendationBuilder recommendationBuilder;

    public AppTodayService(AppMeService appMeService,
                           AppReviewService reviewService,
                           AppOpsService appOpsService,
                           PracticePersistencePort practicePersistence,
                           AppImportTaskService importTaskService,
                           AppTodayVoAssembler todayVoAssembler,
                           AppTodayRecommendationBuilder recommendationBuilder) {
        this.appMeService = appMeService;
        this.reviewService = reviewService;
        this.appOpsService = appOpsService;
        this.practicePersistence = practicePersistence;
        this.importTaskService = importTaskService;
        this.todayVoAssembler = todayVoAssembler;
        this.recommendationBuilder = recommendationBuilder;
    }

    /**
     * 聚合今日页完整数据。
     */
    public AppTodayVO buildToday() {
        requireUserId();
        AppMeResponse me = appMeService.buildMe();
        Long userId = UserContext.currentUserId();
        Long tenantId = TenantContext.currentTenantId();
        LocalDate today = LocalDate.now();
        int completed = practicePersistence.countTodayAnswers(tenantId, userId, today);

        var pending = reviewService.pendingSummary(5);
        var resume = practicePersistence.findLatestInProgress(tenantId, userId)
                .map(todayVoAssembler::resumeLearning)
                .orElse(null);

        return todayVoAssembler.assemble(
                todayVoAssembler.welcome(me, completed),
                todayVoAssembler.statCards(completed),
                resume,
                todayVoAssembler.reviewSection(pending),
                recommendationBuilder.buildFromPendingItems(pending.getItems(), 2),
                importTaskService.latestActiveStatus(),
                appOpsService.listActiveBanners("HOME_TOP"),
                appOpsService.listPublishedAnnouncements());
    }

    private void requireUserId() {
        if (UserContext.currentUserId() == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
    }
}
