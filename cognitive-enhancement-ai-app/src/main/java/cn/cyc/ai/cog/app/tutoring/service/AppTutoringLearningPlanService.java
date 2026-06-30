package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.dto.AppLearningPlanVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.tutoring.entity.LearningPlanEntity;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 长期学习计划服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringLearningPlanService {

    /**
     * 学习辅导持久化服务提供者。
     */
    private final ObjectProvider<TutoringPersistenceService> persistenceProvider;

    /**
     * JSON 序列化器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 创建学习计划服务。
     *
     * @param persistenceProvider 学习辅导持久化服务提供者
     * @param objectMapper        JSON 序列化器
     */
    public AppTutoringLearningPlanService(ObjectProvider<TutoringPersistenceService> persistenceProvider,
                                          ObjectMapper objectMapper) {
        this.persistenceProvider = persistenceProvider;
        this.objectMapper = objectMapper;
    }

    /**
     * 根据教学蓝图保存学习计划。
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @param traceId   链路追踪 ID
     * @param blueprint 教学蓝图
     * @return 新建计划 ID，条件不满足时返回 null
     */
    public Long savePlan(Long userId, String sessionId, String traceId, AppTutoringBlueprint blueprint) {
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null || userId == null || blueprint == null) {
            return null;
        }
        String title = StringUtils.hasText(blueprint.getLearningGoal())
                ? blueprint.getLearningGoal() : "学习提升计划";
        return persistence.saveLearningPlan(userId, sessionId, traceId, title, blueprint.getTeachingPlan());
    }

    /**
     * 查询当前用户活跃的学习计划。
     *
     * @return 活跃学习计划，不存在时返回 null
     */
    public AppLearningPlanVO findActiveForCurrentUser() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return null;
        }
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null) {
            return null;
        }
        LearningPlanEntity entity = persistence.findActiveLearningPlan(userId);
        return entity == null ? null : toVo(entity);
    }

    /**
     * 构建注入提示词的学习计划段落。
     *
     * @return 计划 JSON 文本，无活跃计划时返回 null
     */
    public String buildPlanPromptSection() {
        AppLearningPlanVO plan = findActiveForCurrentUser();
        if (plan == null || !StringUtils.hasText(plan.getPlanJson())) {
            return null;
        }
        return plan.getPlanJson();
    }

    /**
     * 转换为Vo。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private AppLearningPlanVO toVo(LearningPlanEntity entity) {
        AppLearningPlanVO vo = new AppLearningPlanVO();
        vo.setId(entity.getId());
        vo.setSessionId(entity.getSessionId());
        vo.setTraceId(entity.getTraceId());
        vo.setPlanTitle(entity.getPlanTitle());
        vo.setStatus(entity.getStatus());
        vo.setPlanJson(entity.getPlanJson());
        try {
            List<AppLearningPlanVO.PlanStage> stages = objectMapper.readValue(
                    entity.getPlanJson(), new TypeReference<List<AppLearningPlanVO.PlanStage>>() {});
            vo.setStages(stages);
        } catch (Exception ignored) {
            // 计划 JSON 非 stage 列表时仅保留原文
        }
        return vo;
    }
}
