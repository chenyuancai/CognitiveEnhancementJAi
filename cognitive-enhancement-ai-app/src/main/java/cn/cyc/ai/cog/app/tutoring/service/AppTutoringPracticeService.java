package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.dto.AppPracticeRecommendationVO;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.tutoring.entity.PracticeRecommendationEntity;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 练习推荐服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringPracticeService {

    /**
     * 学习辅导持久化服务提供者。
     */
    private final ObjectProvider<TutoringPersistenceService> persistenceProvider;

    /**
     * 创建练习推荐服务。
     *
     * @param persistenceProvider 学习辅导持久化服务提供者
     */
    public AppTutoringPracticeService(ObjectProvider<TutoringPersistenceService> persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
    }

    /**
     * 在辅导流程中保存一条练习推荐记录。
     *
     * @param userId          用户 ID
     * @param sessionId       会话 ID
     * @param traceId         链路追踪 ID
     * @param knowledgePoint  知识点
     * @param promptText      练习提示文本
     */
    public void recommend(Long userId,
                          String sessionId,
                          String traceId,
                          String knowledgePoint,
                          String promptText) {
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null || userId == null || !StringUtils.hasText(promptText)) {
            return;
        }
        persistence.savePracticeRecommendation(userId, sessionId, traceId, knowledgePoint, promptText, "EASY");
    }

    /**
     * 查询指定会话下当前用户待完成的练习推荐。
     *
     * @param sessionId 会话 ID
     * @return 待完成练习列表，未登录或无持久化服务时返回空列表
     */
    public List<AppPracticeRecommendationVO> listPendingForSession(String sessionId) {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return List.of();
        }
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null) {
            return List.of();
        }
        return persistence.listPendingPractice(userId, sessionId).stream().map(this::toVo).toList();
    }

    /**
     * 转换为Vo。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private AppPracticeRecommendationVO toVo(PracticeRecommendationEntity entity) {
        AppPracticeRecommendationVO vo = new AppPracticeRecommendationVO();
        vo.setId(entity.getId());
        vo.setSessionId(entity.getSessionId());
        vo.setKnowledgePoint(entity.getKnowledgePoint());
        vo.setPromptText(entity.getPromptText());
        vo.setDifficulty(entity.getDifficulty());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
