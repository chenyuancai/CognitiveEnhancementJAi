package cn.cyc.ai.cog.core.harness;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;

import java.util.Map;

/**
 * 输出治理器，负责运行时执行结果的标准化治理。
 *
 * <p>一期的职责：
 * <ul>
 *   <li>输出格式标准化（统一 JSON 结构）</li>
 *   <li>输出 Schema 校验</li>
 *   <li>敏感信息脱敏（日志级别）</li>
 * </ul>
 *
 * <p>二期可扩展：内容安全审核、输出缓存、多语言适配。
 *
 * @author cyc
 */
public interface OutputGovernance {

    /**
     * 治理输出结果，返回标准化后的执行结果。
     *
     * @param rawResult    原始执行结果
     * @param outputSchema 期望的输出结构定义（可为 null）
     * @return 治理后的执行结果
     */
    ExecutionResult govern(ExecutionResult rawResult, Map<String, Object> outputSchema);

    /**
     * 对输出内容进行脱敏处理（用于日志/记录）。
     *
     * @param content 原始内容
     * @return 脱敏后的内容
     */
    String sanitizeForLog(String content);
}
