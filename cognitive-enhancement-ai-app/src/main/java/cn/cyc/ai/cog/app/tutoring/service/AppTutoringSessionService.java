package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringSessionPageQuery;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringSessionSummaryVO;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringConstants;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringTenantSync;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.service.ConversationSessionService;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * C 端学习辅导会话查询服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringSessionService {

    /**
     * 运行时会话服务。
     */
    private final ConversationSessionService conversationSessionService;

    /**
     * 会话消息仓储。
     */
    private final ConversationMessageRepository conversationMessageRepository;

    /**
     * 创建学习辅导会话查询服务。
     *
     * @param conversationSessionService   运行时会话服务
     * @param conversationMessageRepository 会话消息仓储
     */
    public AppTutoringSessionService(ConversationSessionService conversationSessionService,
                                     ConversationMessageRepository conversationMessageRepository) {
        this.conversationSessionService = conversationSessionService;
        this.conversationMessageRepository = conversationMessageRepository;
    }

    /**
     * 分页查询当前用户的学习辅导会话列表。
     *
     * @param query 分页查询条件，可为空
     * @return 会话摘要分页结果
     */
    public PageResult<AppTutoringSessionSummaryVO> pageSessions(AppTutoringSessionPageQuery query) {
        AppTutoringSessionPageQuery body = query == null ? new AppTutoringSessionPageQuery() : query;
        return AppTutoringTenantSync.runWithRuntimeTenant(() -> {
            PageResult<ConversationSession> page = conversationSessionService.pageMySessions(
                    body.getCurrent(), body.getSize(), AppTutoringConstants.SESSION_CAPABILITY_CODE);
            return page.map(this::toSummary);
        });
    }

    /**
     * 将会话实体转换为摘要视图对象。
     *
     * @param session 会话实体
     * @return 会话摘要
     */
    private AppTutoringSessionSummaryVO toSummary(ConversationSession session) {
        AppTutoringSessionSummaryVO vo = new AppTutoringSessionSummaryVO();
        vo.setSessionId(session.sessionId());
        vo.setTitle(session.title());
        vo.setCapabilityCode(session.capabilityCode());
        vo.setStatus(session.status().name());
        vo.setCreatedAt(session.createdAt());
        vo.setUpdatedAt(session.updatedAt());
        Optional<ConversationMessage> latest = conversationMessageRepository.findLatestBySessionId(session.sessionId());
        vo.setLastMessagePreview(latest.map(message -> preview(message.content())).orElse(null));
        return vo;
    }

    /**
     * 截取消息内容作为列表预览文本。
     *
     * @param content 原始消息内容
     * @return 最多 80 字的预览文本，空白时返回 null
     */
    private String preview(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }
        String compact = content.trim();
        if (compact.length() <= 80) {
            return compact;
        }
        return compact.substring(0, 80) + "…";
    }
}
