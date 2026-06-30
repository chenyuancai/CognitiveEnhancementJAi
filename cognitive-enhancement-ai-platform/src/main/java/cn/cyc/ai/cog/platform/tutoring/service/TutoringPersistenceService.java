package cn.cyc.ai.cog.platform.tutoring.service;

import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.tutoring.entity.ConversationSummaryEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.LearningPlanEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.LearningProfileEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.LearningStateSnapshotEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.MessageReferenceEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.MistakeRecordEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.PracticeRecommendationEntity;
import cn.cyc.ai.cog.platform.tutoring.entity.TutoringBlueprintEntity;
import cn.cyc.ai.cog.platform.tutoring.mapper.ConversationSummaryMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.LearningPlanMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.LearningProfileMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.LearningStateSnapshotMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.MessageReferenceMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.MistakeRecordMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.PracticeRecommendationMapper;
import cn.cyc.ai.cog.platform.tutoring.mapper.TutoringBlueprintMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI 学习辅导审计与摘要持久化服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class TutoringPersistenceService {

    /** 辅导蓝图 Mapper。 */
    private final TutoringBlueprintMapper tutoringBlueprintMapper;

    /** 学习状态快照 Mapper。 */
    private final LearningStateSnapshotMapper learningStateSnapshotMapper;

    /** 会话摘要 Mapper。 */
    private final ConversationSummaryMapper conversationSummaryMapper;

    /** 学习画像 Mapper。 */
    private final LearningProfileMapper learningProfileMapper;

    /** 错题记录 Mapper。 */
    private final MistakeRecordMapper mistakeRecordMapper;

    /** 学习计划 Mapper。 */
    private final LearningPlanMapper learningPlanMapper;

    /** 练习推荐 Mapper。 */
    private final PracticeRecommendationMapper practiceRecommendationMapper;

    /** 消息引用 Mapper。 */
    private final MessageReferenceMapper messageReferenceMapper;

    /** JSON 序列化工具。 */
    private final ObjectMapper objectMapper;

    /**
     * 创建辅导持久化服务。
     *
     * @param tutoringBlueprintMapper        辅导蓝图 Mapper
     * @param learningStateSnapshotMapper    学习状态快照 Mapper
     * @param conversationSummaryMapper      会话摘要 Mapper
     * @param learningProfileMapper          学习画像 Mapper
     * @param mistakeRecordMapper            错题记录 Mapper
     * @param learningPlanMapper             学习计划 Mapper
     * @param practiceRecommendationMapper   练习推荐 Mapper
     * @param messageReferenceMapper         消息引用 Mapper
     * @param objectMapper                   JSON 序列化工具
     */
    public TutoringPersistenceService(TutoringBlueprintMapper tutoringBlueprintMapper,
                                      LearningStateSnapshotMapper learningStateSnapshotMapper,
                                      ConversationSummaryMapper conversationSummaryMapper,
                                      LearningProfileMapper learningProfileMapper,
                                      MistakeRecordMapper mistakeRecordMapper,
                                      LearningPlanMapper learningPlanMapper,
                                      PracticeRecommendationMapper practiceRecommendationMapper,
                                      MessageReferenceMapper messageReferenceMapper,
                                      ObjectMapper objectMapper) {
        this.tutoringBlueprintMapper = tutoringBlueprintMapper;
        this.learningStateSnapshotMapper = learningStateSnapshotMapper;
        this.conversationSummaryMapper = conversationSummaryMapper;
        this.learningProfileMapper = learningProfileMapper;
        this.mistakeRecordMapper = mistakeRecordMapper;
        this.learningPlanMapper = learningPlanMapper;
        this.practiceRecommendationMapper = practiceRecommendationMapper;
        this.messageReferenceMapper = messageReferenceMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 保存辅导蓝图审计记录。
     *
     * @param sessionId 会话 ID
     * @param traceId   追踪 ID
     * @param messageId 消息 ID
     * @param intent    学生意图
     * @param strategy  教学策略
     * @param blueprint 蓝图对象，将序列化为 JSON 存储
     */
    /**
     * 执行save蓝图。
     */
    @Transactional
    public void saveBlueprint(String sessionId, String traceId, String messageId,
                              String intent, String strategy, Object blueprint) {
        TutoringBlueprintEntity entity = new TutoringBlueprintEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setSessionId(sessionId);
        entity.setTraceId(traceId);
        entity.setMessageId(messageId);
        entity.setIntent(intent);
        entity.setStrategy(strategy);
        entity.setBlueprintJson(writeJson(blueprint));
        tutoringBlueprintMapper.insert(entity);
    }

    /**
     * 保存学习状态快照。
     *
     * @param sessionId 会话 ID
     * @param traceId   追踪 ID
     * @param state     学习状态对象，将序列化为 JSON 存储
     */
    /**
     * 执行save学习状态快照。
     *
     * @param sessionId 会话 ID
     * @param traceId 链路 Trace ID
     * @param state 状态
     */
    @Transactional
    public void saveLearningStateSnapshot(String sessionId, String traceId, Object state) {
        LearningStateSnapshotEntity entity = new LearningStateSnapshotEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setSessionId(sessionId);
        entity.setTraceId(traceId);
        entity.setStateJson(writeJson(state));
        learningStateSnapshotMapper.insert(entity);
    }

    /**
     * 新增或更新会话摘要。
     *
     * @param sessionId   会话 ID
     * @param summaryText 摘要文本
     */
    /**
     * 执行upsertConversation摘要。
     *
     * @param sessionId 会话 ID
     * @param summaryText 摘要Text
     */
    @Transactional
    public void upsertConversationSummary(String sessionId, String summaryText) {
        Long tenantId = TenantContext.currentTenantId();
        ConversationSummaryEntity existing = conversationSummaryMapper.selectOne(
                new LambdaQueryWrapper<ConversationSummaryEntity>()
                        .eq(ConversationSummaryEntity::getTenantId, tenantId)
                        .eq(ConversationSummaryEntity::getSessionId, sessionId));
        if (existing == null) {
            ConversationSummaryEntity entity = new ConversationSummaryEntity();
            entity.setTenantId(tenantId);
            entity.setSessionId(sessionId);
            entity.setSummaryText(summaryText);
            entity.setVersionNo(1);
            conversationSummaryMapper.insert(entity);
            return;
        }
        existing.setSummaryText(summaryText);
        existing.setVersionNo(existing.getVersionNo() == null ? 1 : existing.getVersionNo() + 1);
        conversationSummaryMapper.updateById(existing);
    }

    /**
     * 查询指定会话的摘要文本。
     *
     * @param sessionId 会话 ID
     * @return 摘要文本，不存在时返回 {@code null}
     */
    public String findConversationSummary(String sessionId) {
        ConversationSummaryEntity entity = conversationSummaryMapper.selectOne(
                new LambdaQueryWrapper<ConversationSummaryEntity>()
                        .eq(ConversationSummaryEntity::getTenantId, TenantContext.currentTenantId())
                        .eq(ConversationSummaryEntity::getSessionId, sessionId));
        return entity == null ? null : entity.getSummaryText();
    }

    /**
     * 新增或更新用户学习画像。
     *
     * @param userId  用户 ID
     * @param profile 画像对象，将序列化为 JSON 存储
     */
    /**
     * 执行upsert学习画像。
     *
     * @param userId 用户 ID
     * @param profile 画像
     */
    @Transactional
    public void upsertLearningProfile(Long userId, Object profile) {
        Long tenantId = TenantContext.currentTenantId();
        LearningProfileEntity existing = learningProfileMapper.selectOne(
                new LambdaQueryWrapper<LearningProfileEntity>()
                        .eq(LearningProfileEntity::getTenantId, tenantId)
                        .eq(LearningProfileEntity::getUserId, userId));
        if (existing == null) {
            LearningProfileEntity entity = new LearningProfileEntity();
            entity.setTenantId(tenantId);
            entity.setUserId(userId);
            entity.setProfileJson(writeJson(profile));
            entity.setVersionNo(1);
            learningProfileMapper.insert(entity);
            return;
        }
        existing.setProfileJson(writeJson(profile));
        existing.setVersionNo(existing.getVersionNo() == null ? 1 : existing.getVersionNo() + 1);
        learningProfileMapper.updateById(existing);
    }

    /**
     * 查询并反序列化用户学习画像。
     *
     * @param userId 用户 ID
     * @param type   目标类型
     * @param <T>    画像类型
     * @return 反序列化后的画像对象，不存在时返回 {@code null}
     */
    public <T> T findLearningProfile(Long userId, Class<T> type) {
        LearningProfileEntity entity = learningProfileMapper.selectOne(
                new LambdaQueryWrapper<LearningProfileEntity>()
                        .eq(LearningProfileEntity::getTenantId, TenantContext.currentTenantId())
                        .eq(LearningProfileEntity::getUserId, userId));
        if (entity == null || entity.getProfileJson() == null) {
            return null;
        }
        return readJson(entity.getProfileJson(), type);
    }

    /**
     * 保存错题记录。
     *
     * @param userId          用户 ID
     * @param sessionId       会话 ID
     * @param traceId         追踪 ID
     * @param knowledgePoint  关联知识点
     * @param mistakeSummary  错题摘要
     * @param userApproach    学生解题思路
     * @param correctionHint  纠正提示
     */
    /**
     * 执行save错题Record。
     */
    @Transactional
    public void saveMistakeRecord(Long userId, String sessionId, String traceId,
                                  String knowledgePoint, String mistakeSummary,
                                  String userApproach, String correctionHint) {
        MistakeRecordEntity entity = new MistakeRecordEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setUserId(userId);
        entity.setSessionId(sessionId);
        entity.setTraceId(traceId);
        entity.setKnowledgePoint(knowledgePoint);
        entity.setMistakeSummary(mistakeSummary);
        entity.setUserApproach(userApproach);
        entity.setCorrectionHint(correctionHint);
        entity.setStatus("OPEN");
        entity.setSourceType("TUTORING");
        mistakeRecordMapper.insert(entity);
    }

    /**
     * 保存练习低分错题记录。
     */
    @Transactional
    public void savePracticeMistakeRecord(Long userId, String sessionId, String traceId,
                                          Long contentId, String title, String tag,
                                          int score, String mistakeSummary) {
        MistakeRecordEntity entity = new MistakeRecordEntity();
        entity.setTenantId(TenantContext.currentTenantId());
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
        mistakeRecordMapper.insert(entity);
    }

    /**
     * 分页查询用户错题记录。
     *
     * @param userId  用户 ID
     * @param current 当前页码
     * @param size    每页条数
     * @return 错题记录分页结果
     */
    public PageResult<MistakeRecordEntity> pageMistakeRecords(Long userId, long current, long size) {
        Page<MistakeRecordEntity> page = mistakeRecordMapper.selectPage(
                new Page<>(current, size),
                new LambdaQueryWrapper<MistakeRecordEntity>()
                        .eq(MistakeRecordEntity::getTenantId, TenantContext.currentTenantId())
                        .eq(MistakeRecordEntity::getUserId, userId)
                        .orderByDesc(MistakeRecordEntity::getCreateTime));
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 保存学习计划，并将该用户已有活跃计划归档。
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @param traceId   追踪 ID
     * @param planTitle 计划标题
     * @param planBody  计划内容对象，将序列化为 JSON 存储
     * @return 新计划主键 ID
     */
    /**
     * 执行save学习计划。
     * @return 执行结果
     */
    @Transactional
    public Long saveLearningPlan(Long userId, String sessionId, String traceId,
                                 String planTitle, Object planBody) {
        archiveActivePlans(userId);
        LearningPlanEntity entity = new LearningPlanEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setUserId(userId);
        entity.setSessionId(sessionId);
        entity.setTraceId(traceId);
        entity.setPlanTitle(planTitle);
        entity.setPlanJson(writeJson(planBody));
        entity.setStatus("ACTIVE");
        learningPlanMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 查询用户当前活跃的学习计划。
     *
     * @param userId 用户 ID
     * @return 活跃计划实体，不存在时返回 {@code null}
     */
    public LearningPlanEntity findActiveLearningPlan(Long userId) {
        return learningPlanMapper.selectOne(
                new LambdaQueryWrapper<LearningPlanEntity>()
                        .eq(LearningPlanEntity::getTenantId, TenantContext.currentTenantId())
                        .eq(LearningPlanEntity::getUserId, userId)
                        .eq(LearningPlanEntity::getStatus, "ACTIVE")
                        .orderByDesc(LearningPlanEntity::getCreateTime)
                        .last("LIMIT 1"));
    }

    /**
     * 保存练习推荐记录。
     *
     * @param userId         用户 ID
     * @param sessionId      会话 ID
     * @param traceId        追踪 ID
     * @param knowledgePoint 关联知识点
     * @param promptText     练习提示文本
     * @param difficulty     难度等级
     */
    /**
     * 执行save练习Recommendation。
     */
    @Transactional
    public void savePracticeRecommendation(Long userId, String sessionId, String traceId,
                                           String knowledgePoint, String promptText, String difficulty) {
        PracticeRecommendationEntity entity = new PracticeRecommendationEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setUserId(userId);
        entity.setSessionId(sessionId);
        entity.setTraceId(traceId);
        entity.setKnowledgePoint(knowledgePoint);
        entity.setPromptText(promptText);
        entity.setDifficulty(difficulty);
        entity.setStatus("PENDING");
        practiceRecommendationMapper.insert(entity);
    }

    /**
     * 查询指定会话下待完成的练习推荐列表。
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @return 待完成练习推荐列表
     */
    public List<PracticeRecommendationEntity> listPendingPractice(Long userId, String sessionId) {
        return practiceRecommendationMapper.selectList(
                new LambdaQueryWrapper<PracticeRecommendationEntity>()
                        .eq(PracticeRecommendationEntity::getTenantId, TenantContext.currentTenantId())
                        .eq(PracticeRecommendationEntity::getUserId, userId)
                        .eq(PracticeRecommendationEntity::getSessionId, sessionId)
                        .eq(PracticeRecommendationEntity::getStatus, "PENDING")
                        .orderByDesc(PracticeRecommendationEntity::getCreateTime));
    }

    /**
     * 批量保存消息引用记录。
     *
     * @param sessionId  会话 ID
     * @param traceId    追踪 ID
     * @param messageId  消息 ID
     * @param references 引用实体列表
     */
    /**
     * 执行save消息References。
     */
    @Transactional
    public void saveMessageReferences(String sessionId, String traceId, String messageId,
                                      List<MessageReferenceEntity> references) {
        Long tenantId = TenantContext.currentTenantId();
        for (MessageReferenceEntity reference : references) {
            reference.setTenantId(tenantId);
            reference.setSessionId(sessionId);
            reference.setTraceId(traceId);
            reference.setMessageId(messageId);
            messageReferenceMapper.insert(reference);
        }
    }

    /**
     * 将用户所有活跃学习计划归档。
     *
     * @param userId 用户 ID
     */
    private void archiveActivePlans(Long userId) {
        List<LearningPlanEntity> activePlans = learningPlanMapper.selectList(
                new LambdaQueryWrapper<LearningPlanEntity>()
                        .eq(LearningPlanEntity::getTenantId, TenantContext.currentTenantId())
                        .eq(LearningPlanEntity::getUserId, userId)
                        .eq(LearningPlanEntity::getStatus, "ACTIVE"));
        for (LearningPlanEntity plan : activePlans) {
            plan.setStatus("ARCHIVED");
            learningPlanMapper.updateById(plan);
        }
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 待序列化对象
     * @return JSON 字符串
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("序列化辅导审计数据失败", ex);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型对象。
     *
     * @param json JSON 字符串
     * @param type 目标类型
     * @param <T>  目标类型参数
     * @return 反序列化后的对象
     */
    private <T> T readJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception ex) {
            throw new IllegalStateException("反序列化辅导审计数据失败", ex);
        }
    }
}
