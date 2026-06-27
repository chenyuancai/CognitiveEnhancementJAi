package cn.cyc.ai.cog.runtime.observation.dto;

import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;

import java.util.List;

/**
 * 单次能力执行链路详情（执行摘要 + 关联用量）。
 *
 * @param execution 执行记录（含输入 / 路由 / 结果）
 * @param usages    同 traceId 的用量记录
 * @author cyc
 */
public record ExecutionRecordDetail(
        ExecutionRecord execution,
        List<UsageRecord> usages
) {
}
