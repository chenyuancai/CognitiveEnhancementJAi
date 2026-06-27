package cn.cyc.ai.cog.platform.common.dto;

/**
 * 按日统计点。
 *
 * @param date  日期（ISO-8601，如 2026-06-22）
 * @param value 指标值
 */
public record DailyPoint(String date, long value) {
}
