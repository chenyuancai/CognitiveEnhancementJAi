package cn.cyc.ai.cog.admin.iam.job;

import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 封禁到期自动恢复兜底定时任务（主路径为登录/鉴权触发）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class UserBanRecoveryJob {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(UserBanRecoveryJob.class);

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /**
     * @param iamUserRepository IAM 用户仓储
     */
    public UserBanRecoveryJob(IamUserRepository iamUserRepository) {
        this.iamUserRepository = iamUserRepository;
    }

    /** 每小时巡检封禁到期用户。 */
    @Scheduled(cron = "0 15 * * * *")
    @SchedulerLock(name = "UserBanRecoveryJob.recoverExpiredBans", lockAtLeastFor = "PT1M", lockAtMostFor = "PT10M")
    public void recoverExpiredBans() {
        int count = iamUserRepository.recoverExpiredBans();
        if (count > 0) {
            log.info("封禁到期自动恢复完成，数量={}", count);
        }
    }
}
