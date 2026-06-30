package cn.cyc.ai.cog.runtime.api;

/**
 * Runtime 最新记录返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LatestRuntimeRecordResult<T>(boolean found, T item) {
}
