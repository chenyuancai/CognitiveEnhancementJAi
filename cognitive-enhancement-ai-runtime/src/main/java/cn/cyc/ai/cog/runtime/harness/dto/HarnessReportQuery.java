package cn.cyc.ai.cog.runtime.harness.dto;

import java.time.Instant;

/**
 * Harness 报告分页查询条件。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessReportQuery(String status, Instant startFrom, Instant startTo) {
}
