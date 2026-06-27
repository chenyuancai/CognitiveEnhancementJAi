package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.platform.billing.repository.FinancialRecordRepository;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.FinancialRecord;
import org.springframework.stereotype.Service;

/**
 * 资金流水查询服务。
 */
@Service
public class FinancialRecordService {

    /** 资金流水仓储 */
    private final FinancialRecordRepository financialRecordRepository;

    /**
     * @param financialRecordRepository 资金流水仓储
     */
    public FinancialRecordService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    /**
     * 分页查询资金流水。
     *
     * @param current   当前页
     * @param size      每页大小
     * @param accountId 商业账户 ID，可为空
     * @param orderId   订单 ID，可为空
     * @return 资金流水分页结果
     */
    public PageResult<FinancialRecord> page(long current, long size, Long accountId, Long orderId) {
        return financialRecordRepository.page(current, size, accountId, orderId);
    }
}
