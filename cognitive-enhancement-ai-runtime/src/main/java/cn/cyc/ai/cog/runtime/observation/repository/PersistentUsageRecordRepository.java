package cn.cyc.ai.cog.runtime.observation.repository;

import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.entity.UsageRecordEntity;
import cn.cyc.ai.cog.runtime.observation.mapper.UsageRecordMapper;
import cn.cyc.ai.cog.runtime.observation.spi.UsageRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 基于 MyBatis Plus 的用量记录仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentUsageRecordRepository implements UsageRecordRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentUsageRecordRepository.class);

    /**
     * 用量记录 Mapper。
     */
    private final UsageRecordMapper usageRecordMapper;

    /**
     * 构造用量记录持久化仓储。
     *
     * @param usageRecordMapper 用量记录 Mapper
     */
    public PersistentUsageRecordRepository(UsageRecordMapper usageRecordMapper) {
        this.usageRecordMapper = usageRecordMapper;
    }

    /**
     * 保存用量记录。
     *
     * @param record 用量记录
     */
    @Override
    public void save(UsageRecord record) {
        usageRecordMapper.insert(toEntity(record));
        log.debug("持久化用量记录, traceId={}, capabilityCode={}, executorType={}",
                record.traceId(), record.capabilityCode(), record.executorType());
    }

    /**
     * 查询全部用量记录（按记录时间倒序）。
     *
     * @return 用量记录列表
     */
    @Override
    public List<UsageRecord> listAll() {
        LambdaQueryWrapper<UsageRecordEntity> queryWrapper = new LambdaQueryWrapper<UsageRecordEntity>()
                .eq(UsageRecordEntity::getTenantId, TenantContext.currentTenantId())
                .orderByDesc(UsageRecordEntity::getRecordedAt)
                .orderByDesc(UsageRecordEntity::getId);
        return usageRecordMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 转换为持久化实体。
     *
     * @param record 用量记录
     * @return 实体
     */
    private UsageRecordEntity toEntity(UsageRecord record) {
        UsageRecordEntity entity = new UsageRecordEntity();
        entity.setTraceId(record.traceId());
        entity.setTenantId(TenantIds.resolveId(record.tenantCode()));
        entity.setCapabilityCode(record.capabilityCode());
        entity.setCapabilityVersion(record.capabilityVersion());
        entity.setAgentCode(record.agentCode());
        entity.setExecutorType(record.executorType());
        entity.setModelCode(record.modelCode());
        entity.setToolCode(record.toolCode());
        entity.setInputTokenCount(record.inputTokenCount());
        entity.setOutputTokenCount(record.outputTokenCount());
        entity.setTotalTokenCount(record.totalTokenCount());
        entity.setEstimatedCostAmount(record.estimatedCostAmount());
        entity.setRecordedAt(record.recordedAt());
        return entity;
    }

    /**
     * 转换为领域对象。
     *
     * @param entity 实体
     * @return 用量记录
     */
    private UsageRecord toDomain(UsageRecordEntity entity) {
        return new UsageRecord(
                entity.getTraceId(),
                TenantIds.toCode(entity.getTenantId()),
                entity.getCapabilityCode(),
                entity.getCapabilityVersion(),
                entity.getAgentCode(),
                entity.getExecutorType(),
                entity.getModelCode(),
                entity.getToolCode(),
                entity.getInputTokenCount() == null ? 0 : entity.getInputTokenCount(),
                entity.getOutputTokenCount() == null ? 0 : entity.getOutputTokenCount(),
                entity.getTotalTokenCount() == null ? 0 : entity.getTotalTokenCount(),
                entity.getEstimatedCostAmount(),
                entity.getRecordedAt()
        );
    }
}
