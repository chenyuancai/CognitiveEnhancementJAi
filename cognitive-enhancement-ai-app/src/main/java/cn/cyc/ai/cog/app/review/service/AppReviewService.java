package cn.cyc.ai.cog.app.review.service;

import cn.cyc.ai.cog.app.contract.AppPageQuery;
import cn.cyc.ai.cog.app.contract.AppPageVO;
import cn.cyc.ai.cog.app.review.assembler.AppReviewVoAssembler;
import cn.cyc.ai.cog.app.review.dto.AppReviewErrorBookItemVO;
import cn.cyc.ai.cog.app.review.dto.AppReviewPendingItemVO;
import cn.cyc.ai.cog.app.review.dto.AppReviewPendingSummaryVO;
import cn.cyc.ai.cog.app.review.dto.AppReviewRecentSessionVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringMistakePageQuery;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringMistakeService;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.practice.spi.PracticePersistencePort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 复习域查询：待复习、错题本、最近练习分页。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppReviewService {

    private final PracticePersistencePort practicePersistence;
    private final AppTutoringMistakeService mistakeService;
    private final AppReviewVoAssembler assembler;

    public AppReviewService(PracticePersistencePort practicePersistence,
                            AppTutoringMistakeService mistakeService,
                            AppReviewVoAssembler assembler) {
        this.practicePersistence = practicePersistence;
        this.mistakeService = mistakeService;
        this.assembler = assembler;
    }

    public AppPageVO<AppReviewPendingItemVO> pagePending(AppPageQuery query) {
        AppPageQuery body = query == null ? new AppPageQuery() : query;
        Long userId = requireUserId();
        return AppPageVO.from(practicePersistence.pageReviewPending(
                TenantContext.currentTenantId(), userId, body.resolvePage(), body.resolveSize()), assembler::toPending);
    }

    public AppPageVO<AppReviewErrorBookItemVO> pageErrorBook(AppPageQuery query) {
        AppPageQuery body = query == null ? new AppPageQuery() : query;
        AppTutoringMistakePageQuery mistakeQuery = new AppTutoringMistakePageQuery();
        mistakeQuery.setCurrent(body.resolvePage());
        mistakeQuery.setSize(body.resolveSize());
        return AppPageVO.from(mistakeService.pageForCurrentUser(mistakeQuery), vo -> assembler.toErrorBook(vo));
    }

    public AppPageVO<AppReviewRecentSessionVO> pageRecentSessions(AppPageQuery query) {
        AppPageQuery body = query == null ? new AppPageQuery() : query;
        Long userId = requireUserId();
        return AppPageVO.from(practicePersistence.pageRecentSessions(
                TenantContext.currentTenantId(), userId, body.resolvePage(), body.resolveSize()), assembler::toRecent);
    }

    public AppReviewPendingSummaryVO pendingSummary(int limit) {
        Long userId = requireUserId();
        Long tenantId = TenantContext.currentTenantId();
        AppReviewPendingSummaryVO summary = new AppReviewPendingSummaryVO();
        summary.setTotal(practicePersistence.countOpenReviewPending(tenantId, userId));
        summary.setOverdueCount(practicePersistence.countOverdueReviewPending(tenantId, userId));
        List<AppReviewPendingItemVO> items = practicePersistence.listTopReviewPending(tenantId, userId, limit).stream()
                .map(assembler::toPending)
                .toList();
        summary.setItems(items);
        return summary;
    }

    public List<AppReviewRecentSessionVO> listRecentForInsights(int limit) {
        Long userId = requireUserId();
        return practicePersistence.pageRecentSessions(TenantContext.currentTenantId(), userId, 1, limit)
                .getRecords().stream().map(assembler::toRecent).toList();
    }

    private Long requireUserId() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
        return userId;
    }
}
