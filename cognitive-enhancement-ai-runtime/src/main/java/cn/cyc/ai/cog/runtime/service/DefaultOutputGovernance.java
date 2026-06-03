package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.harness.OutputGovernance;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OutputGovernance 默认实现。
 *
 * @author cyc
 */
@Component
public class DefaultOutputGovernance implements OutputGovernance {

    /**
     * 治理输出结果，返回原始执行结果。
     *
     * @param rawResult    原始执行结果
     * @param outputSchema 期望的输出结构定义
     * @return 原始执行结果
     */
    @Override
    public ExecutionResult govern(ExecutionResult rawResult, Map<String, Object> outputSchema) {
        return rawResult;
    }

    /**
     * 对输出内容进行脱敏处理，原样返回。
     *
     * @param content 原始内容
     * @return 原始内容
     */
    @Override
    public String sanitizeForLog(String content) {
        return content;
    }
}
