package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 运营看板聚合结果（PRD 6.1）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class OperationDashboardResult {

    /** preset。 */
    private String preset;
    /** rangeStart。 */
    private LocalDateTime rangeStart;
    /** rangeEnd。 */
    private LocalDateTime rangeEnd;
    /** generatedAt。 */
    private LocalDateTime generatedAt = LocalDateTime.now();
    /** 摘要。 */
    private Summary summary = new Summary();
    /** 用户GrowthTrend。 */
    private List<DailyCount> userGrowthTrend = new ArrayList<>();
    /** 会员等级Distribution。 */
    private List<LevelCount> membershipLevelDistribution = new ArrayList<>();
    /** 能力InvocationDistribution。 */
    private List<CapabilityCount> capabilityInvocationDistribution = new ArrayList<>();
    /** 令牌CostTrend。 */
    private List<DailyTokenCost> tokenCostTrend = new ArrayList<>();

    /**
     * Summary
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class Summary {
        /** 总数Users。 */
        private long totalUsers;
        /** newUsers。 */
        private long newUsers;
        /** activeMemberships。 */
        private long activeMemberships;
        /** 总数内容Items。 */
        private long totalContentItems;
        /** importJobsInRange。 */
        private long importJobsInRange;
        /** gmvFen。 */
        private long gmvFen;
        /** 令牌DeltaInRange。 */
        private long tokenDeltaInRange;
        /** aiInvocation数量。 */
        private long aiInvocationCount;
    }

    /**
     * DailyCount
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class DailyCount {
        /** date。 */
        private String date;
        /** newUsers。 */
        private long newUsers;
        /** cumulativeUsers。 */
        private long cumulativeUsers;
    }

    /**
     * LevelCount
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class LevelCount {
        /** 等级编码。 */
        private String levelCode;
        /** 数量。 */
        private long count;
    }

    /**
     * CapabilityCount
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class CapabilityCount {
        /** 能力编码。 */
        private String capabilityCode;
        /** invocation数量。 */
        private long invocationCount;
    }

    /**
     * DailyTokenCost
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class DailyTokenCost {
        /** date。 */
        private String date;
        /** 令牌Delta。 */
        private long tokenDelta;
    }
}
