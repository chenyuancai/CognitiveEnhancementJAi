package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 运营看板聚合结果（PRD 6.1）。
 */
@Data
public class OperationDashboardResult {

    private String preset;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private LocalDateTime generatedAt = LocalDateTime.now();
    private Summary summary = new Summary();
    private List<DailyCount> userGrowthTrend = new ArrayList<>();
    private List<LevelCount> membershipLevelDistribution = new ArrayList<>();
    private List<CapabilityCount> capabilityInvocationDistribution = new ArrayList<>();
    private List<DailyTokenCost> tokenCostTrend = new ArrayList<>();

    @Data
    public static class Summary {
        private long totalUsers;
        private long newUsers;
        private long activeMemberships;
        private long totalContentItems;
        private long importJobsInRange;
        private long gmvFen;
        private long tokenDeltaInRange;
        private long aiInvocationCount;
    }

    @Data
    public static class DailyCount {
        private String date;
        private long newUsers;
        private long cumulativeUsers;
    }

    @Data
    public static class LevelCount {
        private String levelCode;
        private long count;
    }

    @Data
    public static class CapabilityCount {
        private String capabilityCode;
        private long invocationCount;
    }

    @Data
    public static class DailyTokenCost {
        private String date;
        private long tokenDelta;
    }
}
