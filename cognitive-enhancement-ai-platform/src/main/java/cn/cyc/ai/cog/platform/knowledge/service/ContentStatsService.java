package cn.cyc.ai.cog.platform.knowledge.service;

import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import cn.cyc.ai.cog.api.enums.ContentImportJobStatus;
import cn.cyc.ai.cog.api.enums.ContentStatus;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentImportJobRepository;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 内容域只读统计服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ContentStatsService {

    /** 内容仓储 */
    private final ContentRepository contentRepository;

    /** 内容导入任务仓储 */
    private final ContentImportJobRepository contentImportJobRepository;

    /**
     * @param contentRepository          内容仓储
     * @param contentImportJobRepository 内容导入任务仓储
     */
    public ContentStatsService(ContentRepository contentRepository,
                               ContentImportJobRepository contentImportJobRepository) {
        this.contentRepository = contentRepository;
        this.contentImportJobRepository = contentImportJobRepository;
    }

    /**
     * 统计内容总量。
     *
     * @param tenantId 租户 ID
     * @return 内容总量
     */
    public long countContents(Long tenantId) {
        return contentRepository.countByTenant(tenantId);
    }

    /**
     * 按状态统计内容数。
     *
     * @param tenantId 租户 ID
     * @param status   内容状态
     * @return 内容数
     */
    public long countByStatus(Long tenantId, String status) {
        return contentRepository.countByStatus(tenantId, status);
    }

    /**
     * 统计失败导入任务数。
     *
     * @param tenantId 租户 ID
     * @return 失败任务数
     */
    public long countFailedImports(Long tenantId) {
        return contentImportJobRepository.countByStatus(tenantId, ContentImportJobStatus.FAILED.code());
    }

    /**
     * 内容发布趋势（按日已发布内容数）。
     *
     * @param tenantId 租户 ID
     * @param from     起始日期（含）
     * @param to       结束日期（含）
     * @return 按日统计点
     */
    public List<DailyPoint> publishTrend(Long tenantId, LocalDate from, LocalDate to) {
        List<DailyPoint> points = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            LocalDateTime dayStart = cursor.atStartOfDay();
            LocalDateTime dayEnd = cursor.plusDays(1).atStartOfDay().minusNanos(1);
            long count = contentRepository.countByStatusAndUpdateTimeBetween(
                    tenantId, ContentStatus.PUBLISHED.code(), dayStart, dayEnd);
            points.add(new DailyPoint(cursor.toString(), count));
            cursor = cursor.plusDays(1);
        }
        return points;
    }
}
