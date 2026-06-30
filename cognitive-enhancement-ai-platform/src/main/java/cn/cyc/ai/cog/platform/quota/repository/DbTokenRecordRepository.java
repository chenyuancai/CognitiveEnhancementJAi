package cn.cyc.ai.cog.platform.quota.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.quota.domain.TokenRecord;
import cn.cyc.ai.cog.platform.quota.entity.TokenRecordEntity;
import cn.cyc.ai.cog.platform.quota.mapper.TokenRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Token 流水仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbTokenRecordRepository implements TokenRecordRepository {

    /** Token 流水 Mapper */
    private final TokenRecordMapper tokenRecordMapper;

    /**
     * @param tokenRecordMapper Token 流水 Mapper
     */
    public DbTokenRecordRepository(TokenRecordMapper tokenRecordMapper) {
        this.tokenRecordMapper = tokenRecordMapper;
    }

    /**
     * 执行分页。
     *
     * @param current current
     * @param size 大小
     * @param accountId 账户ID
     * @return 执行结果
     */
    @Override
    public PageResult<TokenRecord> page(long current, long size, Long accountId) {
        LambdaQueryWrapper<TokenRecordEntity> wrapper = new LambdaQueryWrapper<>();
        if (accountId != null) {
            wrapper.eq(TokenRecordEntity::getAccountId, accountId);
        }
        wrapper.orderByDesc(TokenRecordEntity::getId);
        Page<TokenRecordEntity> page = tokenRecordMapper.selectPage(Page.of(current, size), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 查找人Idempotency键。
     *
     * @param idempotencyKey idempotency键
     * @return 查找结果
     */
    @Override
    public TokenRecord findByIdempotencyKey(String idempotencyKey) {
        TokenRecordEntity entity = tokenRecordMapper.selectOne(new LambdaQueryWrapper<TokenRecordEntity>()
                .eq(TokenRecordEntity::getIdempotencyKey, idempotencyKey)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    /**
     * 执行insert。
     *
     * @param record record
     */
    @Override
    public void insert(TokenRecord record) {
        TokenRecordEntity entity = new TokenRecordEntity();
        entity.setTenantId(record.tenantId());
        entity.setAccountId(record.accountId());
        entity.setMemberUserId(record.memberUserId());
        entity.setRecordType(record.recordType());
        entity.setBucket(record.bucket());
        entity.setDeltaAmount(record.deltaAmount());
        entity.setBalanceAfter(record.balanceAfter());
        entity.setBizType(record.bizType());
        entity.setBizId(record.bizId());
        entity.setIdempotencyKey(record.idempotencyKey());
        entity.setMessage(record.message());
        entity.setCreateTime(record.createTime());
        tokenRecordMapper.insert(entity);
    }

    /**
     * 执行数量人租户And类型And时间Range。
     * @return 执行结果
     */
    @Override
    public long countByTenantAndTypeAndTimeRange(Long tenantId, String recordType,
                                                 LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<TokenRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TokenRecordEntity::getTenantId, tenantId);
        if (recordType != null) {
            wrapper.eq(TokenRecordEntity::getRecordType, recordType);
        }
        wrapper.ge(TokenRecordEntity::getCreateTime, start);
        wrapper.le(TokenRecordEntity::getCreateTime, end);
        return tokenRecordMapper.selectCount(wrapper);
    }

    /**
     * 查询人租户And时间Range列表。
     *
     * @param tenantId 租户 ID
     * @param start start
     * @param end end
     * @return 结果列表
     */
    @Override
    public List<TokenRecord> listByTenantAndTimeRange(Long tenantId, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<TokenRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TokenRecordEntity::getTenantId, tenantId);
        wrapper.ge(TokenRecordEntity::getCreateTime, start);
        wrapper.le(TokenRecordEntity::getCreateTime, end);
        return tokenRecordMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private TokenRecord toDomain(TokenRecordEntity entity) {
        return new TokenRecord(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getMemberUserId(),
                entity.getRecordType(),
                entity.getBucket(),
                entity.getDeltaAmount(),
                entity.getBalanceAfter(),
                entity.getBizType(),
                entity.getBizId(),
                entity.getIdempotencyKey(),
                entity.getMessage(),
                entity.getCreateTime()
        );
    }
}
