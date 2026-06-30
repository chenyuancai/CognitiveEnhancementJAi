package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.dto.AppMistakeRecordVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringMistakePageQuery;
import cn.cyc.ai.cog.app.tutoring.support.InMemoryMistakeStore;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.tutoring.entity.MistakeRecordEntity;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * 错题本服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringMistakeService {

    /**
     * 学习辅导持久化服务提供者。
     */
    private final ObjectProvider<TutoringPersistenceService> persistenceProvider;

    private final InMemoryMistakeStore inMemoryMistakeStore;

    public AppTutoringMistakeService(ObjectProvider<TutoringPersistenceService> persistenceProvider,
                                     InMemoryMistakeStore inMemoryMistakeStore) {
        this.persistenceProvider = persistenceProvider;
        this.inMemoryMistakeStore = inMemoryMistakeStore;
    }

    /**
     * 在辅导流程中按需保存错题记录。
     *
     * @param userId          用户 ID
     * @param sessionId       会话 ID
     * @param traceId         链路追踪 ID
     * @param knowledgePoint  知识点
     * @param mistakeSummary  错误摘要
     * @param userApproach    学生解题思路
     * @param correctionHint  纠正提示
     */
    public void saveIfNeeded(Long userId,
                             String sessionId,
                             String traceId,
                             String knowledgePoint,
                             String mistakeSummary,
                             String userApproach,
                             String correctionHint) {
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null || userId == null) {
            return;
        }
        persistence.saveMistakeRecord(userId, sessionId, traceId, knowledgePoint,
                mistakeSummary, userApproach, correctionHint);
    }

    /**
     * 练习低分写入错题本。
     */
    public void saveFromPractice(Long userId, String sessionId, String traceId, Long contentId,
                                 String title, String tag, int score, String mistakeSummary) {
        if (userId == null) {
            return;
        }
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence != null) {
            persistence.savePracticeMistakeRecord(userId, sessionId, traceId, contentId, title, tag, score, mistakeSummary);
            return;
        }
        MistakeRecordEntity entity = new MistakeRecordEntity();
        entity.setTenantId(1L);
        entity.setUserId(userId);
        entity.setSessionId(sessionId);
        entity.setTraceId(traceId);
        entity.setKnowledgePoint(title);
        entity.setContentId(contentId);
        entity.setScore(score);
        entity.setTag(tag);
        entity.setMistakeSummary(mistakeSummary);
        entity.setStatus("OPEN");
        entity.setSourceType("PRACTICE");
        inMemoryMistakeStore.save(entity);
    }

    /**
     * 分页查询当前用户的错题记录。
     *
     * @param query 分页查询条件
     * @return 错题记录分页结果，未登录时返回空页
     */
    public PageResult<AppMistakeRecordVO> pageForCurrentUser(AppTutoringMistakePageQuery query) {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return PageResult.empty(query.getCurrent(), query.getSize());
        }
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null) {
            return inMemoryMistakeStore.page(userId, query.getCurrent(), query.getSize()).map(this::toVo);
        }
        return persistence.pageMistakeRecords(userId, query.getCurrent(), query.getSize())
                .map(this::toVo);
    }

    /**
     * 转换为Vo。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private AppMistakeRecordVO toVo(MistakeRecordEntity entity) {
        AppMistakeRecordVO vo = new AppMistakeRecordVO();
        vo.setId(entity.getId());
        vo.setSessionId(entity.getSessionId());
        vo.setTraceId(entity.getTraceId());
        vo.setKnowledgePoint(entity.getKnowledgePoint());
        vo.setMistakeSummary(entity.getMistakeSummary());
        vo.setUserApproach(entity.getUserApproach());
        vo.setCorrectionHint(entity.getCorrectionHint());
        vo.setContentId(entity.getContentId());
        vo.setScore(entity.getScore());
        vo.setTag(entity.getTag());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
