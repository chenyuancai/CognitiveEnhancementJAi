package cn.cyc.ai.cog.runtime.feedback.spi;

import cn.cyc.ai.cog.runtime.feedback.domain.ExecutionFeedback;

import java.util.List;

/**
 * 执行反馈仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ExecutionFeedbackRepository {

    /**
     * 保存执行反馈。
     *
     * @param feedback 执行反馈
     */
    void save(ExecutionFeedback feedback);

    /**
     * 按 TraceId 查询当前租户反馈列表。
     *
     * @param traceId 链路 TraceId
     * @return 反馈列表
     */
    List<ExecutionFeedback> findByTraceId(String traceId);

    /**
     * 按会话 ID 查询当前租户反馈列表。
     *
     * @param sessionId 会话 ID
     * @return 反馈列表
     */
    List<ExecutionFeedback> findBySessionId(String sessionId);
}
