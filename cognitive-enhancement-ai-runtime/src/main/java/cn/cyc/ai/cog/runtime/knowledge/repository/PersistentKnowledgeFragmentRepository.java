package cn.cyc.ai.cog.runtime.knowledge.repository;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;
import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragmentStatus;
import cn.cyc.ai.cog.runtime.knowledge.entity.KnowledgeFragmentEntity;
import cn.cyc.ai.cog.runtime.knowledge.mapper.KnowledgeFragmentMapper;
import cn.cyc.ai.cog.runtime.knowledge.spi.KnowledgeFragmentRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 持久化知识片段仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentKnowledgeFragmentRepository implements KnowledgeFragmentRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentKnowledgeFragmentRepository.class);

    /**
     * 标签 JSON 类型引用。
     */
    private static final TypeReference<List<String>> TAGS_TYPE = new TypeReference<>() {
    };

    /**
     * 知识片段 Mapper。
     */
    private final KnowledgeFragmentMapper knowledgeFragmentMapper;

    /**
     * JSON 序列化器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造持久化知识片段仓储。
     *
     * @param knowledgeFragmentMapper 知识片段 Mapper
     * @param objectMapper            JSON 序列化器
     */
    public PersistentKnowledgeFragmentRepository(KnowledgeFragmentMapper knowledgeFragmentMapper,
                                                 ObjectMapper objectMapper) {
        this.knowledgeFragmentMapper = knowledgeFragmentMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行save。
     *
     * @param fragment fragment
     */
    @Override
    public void save(KnowledgeFragment fragment) {
        knowledgeFragmentMapper.insert(toEntity(fragment));
        log.debug("持久化知识片段, fragmentId={}, knowledgeCode={}", fragment.fragmentId(), fragment.knowledgeCode());
    }

    /**
     * 查找人FragmentID。
     *
     * @param fragmentId fragmentID
     * @return 查找结果
     */
    @Override
    public Optional<KnowledgeFragment> findByFragmentId(String fragmentId) {
        LambdaQueryWrapper<KnowledgeFragmentEntity> queryWrapper = new LambdaQueryWrapper<KnowledgeFragmentEntity>()
                .eq(KnowledgeFragmentEntity::getTenantId, TenantContext.currentTenantId())
                .eq(KnowledgeFragmentEntity::getFragmentId, fragmentId);
        return Optional.ofNullable(knowledgeFragmentMapper.selectOne(queryWrapper))
                .map(this::toDomain);
    }

    /**
     * 查找人知识编码。
     *
     * @param knowledgeCode 知识编码
     * @return 查找结果
     */
    @Override
    public List<KnowledgeFragment> findByKnowledgeCode(String knowledgeCode) {
        LambdaQueryWrapper<KnowledgeFragmentEntity> queryWrapper = new LambdaQueryWrapper<KnowledgeFragmentEntity>()
                .eq(KnowledgeFragmentEntity::getTenantId, TenantContext.currentTenantId())
                .eq(KnowledgeFragmentEntity::getKnowledgeCode, knowledgeCode)
                .orderByDesc(KnowledgeFragmentEntity::getRecordedAt)
                .orderByDesc(KnowledgeFragmentEntity::getId);
        return knowledgeFragmentMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<KnowledgeFragment> listAll() {
        LambdaQueryWrapper<KnowledgeFragmentEntity> queryWrapper = new LambdaQueryWrapper<KnowledgeFragmentEntity>()
                .eq(KnowledgeFragmentEntity::getTenantId, TenantContext.currentTenantId())
                .orderByDesc(KnowledgeFragmentEntity::getRecordedAt)
                .orderByDesc(KnowledgeFragmentEntity::getId);
        return knowledgeFragmentMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 转换为实体。
     *
     * @param fragment fragment
     * @return 转换结果
     */
    private KnowledgeFragmentEntity toEntity(KnowledgeFragment fragment) {
        KnowledgeFragmentEntity entity = new KnowledgeFragmentEntity();
        entity.setTenantId(TenantIds.resolveId(fragment.tenantCode()));
        entity.setFragmentId(fragment.fragmentId());
        entity.setKnowledgeCode(fragment.knowledgeCode());
        entity.setTitle(fragment.title());
        entity.setContent(fragment.content());
        entity.setTagsJson(serializeTags(fragment.tags()));
        entity.setStatus(fragment.status().name());
        entity.setRecordedAt(fragment.recordedAt());
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private KnowledgeFragment toDomain(KnowledgeFragmentEntity entity) {
        return new KnowledgeFragment(
                TenantIds.toCode(entity.getTenantId()),
                entity.getFragmentId(),
                entity.getKnowledgeCode(),
                entity.getTitle(),
                entity.getContent(),
                deserializeTags(entity.getTagsJson()),
                KnowledgeFragmentStatus.valueOf(entity.getStatus()),
                entity.getRecordedAt()
        );
    }

    /**
     * 执行serializeTags。
     *
     * @param tags tags
     * @return 执行结果
     */
    private String serializeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException exception) {
            log.warn("知识片段标签 JSON 序列化失败", exception);
            return null;
        }
    }

    /**
     * 执行deserializeTags。
     *
     * @param tagsJson tagsJSON
     * @return 执行结果
     */
    private List<String> deserializeTags(String tagsJson) {
        if (tagsJson == null || tagsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(tagsJson, TAGS_TYPE);
        } catch (JsonProcessingException exception) {
            log.warn("知识片段标签 JSON 反序列化失败", exception);
            return Collections.emptyList();
        }
    }
}
