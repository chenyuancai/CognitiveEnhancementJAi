package cn.cyc.ai.cog.admin.billing.job;

import cn.cyc.ai.cog.platform.billing.service.BillingLifecycleService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 计费生命周期定时任务：订阅过期、周期额度重置、待支付超时关闭。
 */
@Component
public class BillingLifecycleJob {

    /** 计费生命周期服务 */
    private final BillingLifecycleService billingLifecycleService;

    /**
     * @param billingLifecycleService 计费生命周期服务
     */
    public BillingLifecycleJob(BillingLifecycleService billingLifecycleService) {
        this.billingLifecycleService = billingLifecycleService;
    }

    /** 每小时处理订阅过期。 */
    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(name = "BillingLifecycleJob.expireSubscriptions", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    public void expireSubscriptions() {
        billingLifecycleService.expireSubscriptions();
    }

    /** 每日凌晨重置周期额度。 */
    @Scheduled(cron = "0 0 1 * * *")
    @SchedulerLock(name = "BillingLifecycleJob.resetCycleQuotas", lockAtLeastFor = "PT1M", lockAtMostFor = "PT15M")
    public void resetCycleQuotas() {
        billingLifecycleService.resetCycleQuotas();
    }

    /** 每 5 分钟关闭超时待支付订单。 */
    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "BillingLifecycleJob.closeExpiredPendingOrders", lockAtLeastFor = "PT1M", lockAtMostFor = "PT10M")
    public void closeExpiredPendingOrders() {
        billingLifecycleService.closeExpiredPendingOrders();
    }
}
