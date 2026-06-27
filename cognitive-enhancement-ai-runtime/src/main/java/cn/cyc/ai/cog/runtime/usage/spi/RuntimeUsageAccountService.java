package cn.cyc.ai.cog.runtime.usage.spi;

import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;

/**
 * 运行时用量额度账户服务。
 *
 * @author cyc
 */
public interface RuntimeUsageAccountService {

    /**
     * 执行前检查当前租户额度。
     *
     * @param capabilityCode 能力编码
     */
    void checkBeforeExecution(String capabilityCode);

    /**
     * 按实际用量记录扣减当前租户额度。
     *
     * @param usageRecord 用量记录
     * @return 扣减后的账户
     */
    UsageAccount recordUsage(UsageRecord usageRecord);

    /**
     * 查询当前租户额度账户。
     *
     * @return 当前租户账户
     */
    UsageAccount currentAccount();
}
