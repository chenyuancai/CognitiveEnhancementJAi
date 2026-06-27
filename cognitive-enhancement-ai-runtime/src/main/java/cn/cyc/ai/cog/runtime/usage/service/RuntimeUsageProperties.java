package cn.cyc.ai.cog.runtime.usage.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Runtime 用量与额度配置。
 *
 * @author cyc
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.usage")
public class RuntimeUsageProperties {

    /**
     * 额度账户配置。
     */
    private Account account = new Account();

    /**
     * 成本计算配置。
     */
    private Cost cost = new Cost();

    /**
     * 额度账户配置。
     */
    @Data
    public static class Account {

        /**
         * 是否启用额度账户拦截与扣减。
         */
        private boolean enabled = false;

        /**
         * 额度后端：legacy（qz_rt_usage_account）或 admin-quota（qz_mbr_quota_account）。
         */
        private String backend = "legacy";

        /**
         * 自动创建租户账户时的默认余额。
         */
        private BigDecimal defaultBalanceAmount = new BigDecimal("100.000000");

        /**
         * 调用前预检成本。
         */
        private BigDecimal preflightCostAmount = new BigDecimal("1.000000");
    }

    /**
     * 成本计算配置。
     */
    @Data
    public static class Cost {

        /**
         * Tool 单次调用成本。
         */
        private BigDecimal toolCallCostAmount = new BigDecimal("1.000000");

        /**
         * LLM 单 token 成本。
         */
        private BigDecimal llmTokenCostAmount = new BigDecimal("0.000010");
    }
}
