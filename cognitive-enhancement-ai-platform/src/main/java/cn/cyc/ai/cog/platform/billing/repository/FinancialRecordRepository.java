package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.FinancialRecord;

/**
 * 资金流水仓储接口。
 */
public interface FinancialRecordRepository {

    /**
     * 分页查询资金流水。
     *
     * @param current   当前页
     * @param size      每页大小
     * @param accountId 商业账户 ID，可为空
     * @param orderId   订单 ID，可为空
     * @return 资金流水分页结果
     */
    PageResult<FinancialRecord> page(long current, long size, Long accountId, Long orderId);

    /**
     * 记录支付流水。
     *
     * @param tenantId  租户 ID
     * @param accountId 商业账户 ID
     * @param orderId   订单 ID
     * @param amountFen 金额（分）
     * @param remark    备注
     */
    void insertPayment(Long tenantId, Long accountId, Long orderId, Long amountFen, String remark);

    /**
     * 记录退款流水。
     *
     * @param tenantId          租户 ID
     * @param accountId         商业账户 ID
     * @param orderId           订单 ID
     * @param refundAmountFen   退款金额（分）
     * @param remark            备注
     */
    void insertRefund(Long tenantId, Long accountId, Long orderId, long refundAmountFen, String remark);

    /**
     * 汇总指定时间范围内的支付流水金额（分）。
     *
     * @param tenantId   租户 ID，可为 null
     * @param recordType 流水类型（如 PAYMENT）
     * @param start      创建时间下限
     * @param end        创建时间上限
     * @return 金额合计（分）
     */
    long sumAmountFen(Long tenantId, String recordType, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
