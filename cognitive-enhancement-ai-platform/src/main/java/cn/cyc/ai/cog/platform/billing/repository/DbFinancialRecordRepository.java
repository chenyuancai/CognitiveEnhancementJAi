package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.support.OperationRecordMessages;
import cn.cyc.ai.cog.platform.billing.domain.FinancialRecord;
import cn.cyc.ai.cog.platform.billing.entity.FinancialRecordEntity;
import cn.cyc.ai.cog.platform.billing.mapper.FinancialRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 资金流水 MyBatis 仓储实现。
 */
/**
 * 资金流水仓储 MyBatis 实现。
 */
@Repository
public class DbFinancialRecordRepository implements FinancialRecordRepository {

    /** 资金流水 Mapper */
    private final FinancialRecordMapper financialRecordMapper;

    /**
     * @param financialRecordMapper 资金流水 Mapper
     */
    public DbFinancialRecordRepository(FinancialRecordMapper financialRecordMapper) {
        this.financialRecordMapper = financialRecordMapper;
    }

    @Override
    public PageResult<FinancialRecord> page(long current, long size, Long accountId, Long orderId) {
        LambdaQueryWrapper<FinancialRecordEntity> wrapper = new LambdaQueryWrapper<>();
        if (accountId != null) {
            wrapper.eq(FinancialRecordEntity::getAccountId, accountId);
        }
        if (orderId != null) {
            wrapper.eq(FinancialRecordEntity::getOrderId, orderId);
        }
        wrapper.orderByDesc(FinancialRecordEntity::getId);
        Page<FinancialRecordEntity> page = financialRecordMapper.selectPage(Page.of(current, size), wrapper);
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    @Override
    public void insertPayment(Long tenantId, Long accountId, Long orderId, Long amountFen, String remark) {
        FinancialRecordEntity record = new FinancialRecordEntity();
        record.setTenantId(tenantId);
        record.setAccountId(accountId);
        record.setOrderId(orderId);
        record.setRecordType("PAYMENT");
        record.setMessage(OperationRecordMessages.financial("PAYMENT", amountFen, orderId));
        record.setAmountFen(amountFen);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        financialRecordMapper.insert(record);
    }

    @Override
    public void insertRefund(Long tenantId, Long accountId, Long orderId, long refundAmountFen, String remark) {
        FinancialRecordEntity record = new FinancialRecordEntity();
        record.setTenantId(tenantId);
        record.setAccountId(accountId);
        record.setOrderId(orderId);
        record.setRecordType("REFUND");
        record.setMessage(OperationRecordMessages.financial("REFUND", -refundAmountFen, orderId));
        record.setAmountFen(-refundAmountFen);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        financialRecordMapper.insert(record);
    }

    @Override
    public long sumAmountFen(Long tenantId, String recordType, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<FinancialRecordEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(FinancialRecordEntity::getTenantId, tenantId);
        }
        if (StringUtils.hasText(recordType)) {
            wrapper.eq(FinancialRecordEntity::getRecordType, recordType);
        }
        if (start != null) {
            wrapper.ge(FinancialRecordEntity::getCreateTime, start);
        }
        if (end != null) {
            wrapper.le(FinancialRecordEntity::getCreateTime, end);
        }
        return financialRecordMapper.selectList(wrapper).stream()
                .mapToLong(r -> r.getAmountFen() == null ? 0L : r.getAmountFen())
                .sum();
    }

    private FinancialRecord toDomain(FinancialRecordEntity entity) {
        return new FinancialRecord(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getOrderId(),
                entity.getRecordType(),
                entity.getMessage(),
                entity.getAmountFen(),
                entity.getBalanceAfterFen(),
                entity.getRemark(),
                entity.getCreateTime()
        );
    }
}
