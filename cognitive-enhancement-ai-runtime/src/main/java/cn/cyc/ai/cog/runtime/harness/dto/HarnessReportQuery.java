package cn.cyc.ai.cog.runtime.harness.dto;

import java.time.Instant;

/**
 * Harness 报告分页查询条件。
 *
 * @param status    整体状态筛选（可选）
 * @param startFrom 开始时间下限（含，可选）
 * @param startTo   开始时间上限（含，可选）
 * @author cyc
 */
public record HarnessReportQuery(String status, Instant startFrom, Instant startTo) {
}
