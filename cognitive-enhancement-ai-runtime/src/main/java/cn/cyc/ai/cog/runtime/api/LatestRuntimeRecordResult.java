package cn.cyc.ai.cog.runtime.api;

/**
 * Runtime 最新记录返回对象。
 *
 * @param found 是否找到记录
 * @param item  最新记录
 * @param <T>   记录类型
 * @author cyc
 */
public record LatestRuntimeRecordResult<T>(boolean found, T item) {
}
