package cn.cyc.ai.cog.runtime.observation.dto;

import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;

import java.util.List;

/**
 * 单次能力执行链路详情（执行摘要 + 关联用量）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ExecutionRecordDetail(
        ExecutionRecord execution,
        List<UsageRecord> usages
) {
}
