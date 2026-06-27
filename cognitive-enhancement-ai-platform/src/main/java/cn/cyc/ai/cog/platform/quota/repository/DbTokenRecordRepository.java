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

    @Override
    public TokenRecord findByIdempotencyKey(String idempotencyKey) {
        TokenRecordEntity entity = tokenRecordMapper.selectOne(new LambdaQueryWrapper<TokenRecordEntity>()
                .eq(TokenRecordEntity::getIdempotencyKey, idempotencyKey)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

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

    @Override
    public List<TokenRecord> listByTenantAndTimeRange(Long tenantId, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<TokenRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TokenRecordEntity::getTenantId, tenantId);
        wrapper.ge(TokenRecordEntity::getCreateTime, start);
        wrapper.le(TokenRecordEntity::getCreateTime, end);
        return tokenRecordMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

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
