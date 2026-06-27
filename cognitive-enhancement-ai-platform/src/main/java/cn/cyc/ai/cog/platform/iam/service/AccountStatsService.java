package cn.cyc.ai.cog.platform.iam.service;

import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 账号域只读统计服务。
 */
@Service
public class AccountStatsService {

    private static final int DEFAULT_ACTIVE_DAYS = 7;

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /**
     * @param iamUserRepository IAM 用户仓储
     */
    public AccountStatsService(IamUserRepository iamUserRepository) {
        this.iamUserRepository = iamUserRepository;
    }

    /**
     * 统计用户总数。
     *
     * @param tenantId 租户 ID
     * @return 用户总数
     */
    public long countUsers(Long tenantId) {
        return iamUserRepository.countUsers(tenantId, null, null);
    }

    /**
     * 统计指定日期新增用户数。
     *
     * @param tenantId 租户 ID
     * @param day      日期
     * @return 新增用户数
     */
    public long countUsersCreatedOn(Long tenantId, LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay().minusNanos(1);
        return iamUserRepository.countUsers(tenantId, start, end);
    }

    /**
     * 统计近 N 日活跃用户数（按登录时间）。
     *
     * @param tenantId 租户 ID
     * @param days     回溯天数
     * @return 活跃用户数
     */
    public long countActiveUsers(Long tenantId, int days) {
        return iamUserRepository.countActiveUsers(tenantId, days <= 0 ? DEFAULT_ACTIVE_DAYS : days);
    }

    /**
     * 用户增长趋势（按日新增）。
     *
     * @param tenantId 租户 ID
     * @param from     起始日期（含）
     * @param to       结束日期（含）
     * @return 按日统计点
     */
    public List<DailyPoint> userGrowth(Long tenantId, LocalDate from, LocalDate to) {
        Map<LocalDate, Long> dailyNew = new LinkedHashMap<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            dailyNew.put(cursor, 0L);
            cursor = cursor.plusDays(1);
        }
        LocalDateTime rangeStart = from.atStartOfDay();
        LocalDateTime rangeEnd = to.plusDays(1).atStartOfDay().minusNanos(1);
        for (IamUser user : iamUserRepository.listUsersCreatedBetween(tenantId, rangeStart, rangeEnd)) {
            if (user.createTime() == null) {
                continue;
            }
            LocalDate day = user.createTime().toLocalDate();
            dailyNew.computeIfPresent(day, (k, v) -> v + 1);
        }
        List<DailyPoint> points = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : dailyNew.entrySet()) {
            points.add(new DailyPoint(entry.getKey().toString(), entry.getValue()));
        }
        return points;
    }
}
