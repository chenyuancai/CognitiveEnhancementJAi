package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.repository.FinancialRecordRepository;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 计费域只读统计服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class BillingStatsService {

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /** 资金流水仓储 */
    private final FinancialRecordRepository financialRecordRepository;

    /**
     * @param orderRepository           订单仓储
     * @param financialRecordRepository 资金流水仓储
     */
    public BillingStatsService(OrderRepository orderRepository,
                               FinancialRecordRepository financialRecordRepository) {
        this.orderRepository = orderRepository;
        this.financialRecordRepository = financialRecordRepository;
    }

    /**
     * 汇总时间范围内支付流水金额（分）。
     *
     * @param tenantId 租户 ID
     * @param from     起始时间（含）
     * @param to       结束时间（含）
     * @return 营收合计（分）
     */
    public long revenueSum(Long tenantId, LocalDateTime from, LocalDateTime to) {
        return financialRecordRepository.sumAmountFen(tenantId, "PAYMENT", from, to);
    }

    /**
     * 按状态统计订单数。
     *
     * @param tenantId 租户 ID
     * @param status   订单状态
     * @return 订单数
     */
    public long countOrders(Long tenantId, String status) {
        return orderRepository.countByStatus(tenantId, status);
    }

    /**
     * 统计订单总数。
     *
     * @param tenantId 租户 ID
     * @return 订单总数
     */
    public long countAllOrders(Long tenantId) {
        return orderRepository.countAll(tenantId);
    }

    /**
     * 营收趋势（按日支付流水汇总，分）。
     *
     * @param tenantId 租户 ID
     * @param from     起始日期（含）
     * @param to       结束日期（含）
     * @return 按日统计点
     */
    public List<DailyPoint> revenueTrend(Long tenantId, LocalDate from, LocalDate to) {
        List<DailyPoint> points = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            LocalDateTime dayStart = cursor.atStartOfDay();
            LocalDateTime dayEnd = cursor.plusDays(1).atStartOfDay().minusNanos(1);
            long amount = financialRecordRepository.sumAmountFen(tenantId, "PAYMENT", dayStart, dayEnd);
            points.add(new DailyPoint(cursor.toString(), amount));
            cursor = cursor.plusDays(1);
        }
        return points;
    }

    /**
     * 统计待支付订单数。
     *
     * @param tenantId 租户 ID
     * @return 待支付订单数
     */
    public long countPendingOrders(Long tenantId) {
        return orderRepository.countByStatus(tenantId, OrderStatus.PENDING.code());
    }
}
