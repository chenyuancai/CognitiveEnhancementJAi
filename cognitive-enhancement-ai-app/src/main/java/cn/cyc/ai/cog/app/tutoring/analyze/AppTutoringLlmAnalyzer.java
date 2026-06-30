package cn.cyc.ai.cog.app.tutoring.analyze;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringLoadedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringLlmAnalysisResult;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * LLM 学习意图与思路分析器（可选能力，失败时回退规则决策）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringLlmAnalyzer {

    /** 日志记录器。 */
    private static final Logger log = LoggerFactory.getLogger(AppTutoringLlmAnalyzer.class);

    /** 运行时能力执行器。 */
    private final RuntimeHarness runtimeHarness;

    /** 分析 Prompt 组装器。 */
    private final AppTutoringAnalyzePromptBuilder promptBuilder;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /** JSON 对象映射器。 */
    private final ObjectMapper objectMapper;

    /**
     * 构造 LLM 分析器。
     *
     * @param runtimeHarness 运行时能力执行器
     * @param promptBuilder  分析 Prompt 组装器
     * @param properties     学习辅导配置属性
     * @param objectMapper   JSON 对象映射器
     */
    public AppTutoringLlmAnalyzer(RuntimeHarness runtimeHarness,
                                  AppTutoringAnalyzePromptBuilder promptBuilder,
                                  AppTutoringProperties properties,
                                  ObjectMapper objectMapper) {
        this.runtimeHarness = runtimeHarness;
        this.promptBuilder = promptBuilder;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 在配置启用时执行 LLM 分析，否则返回空结果。
     *
     * @param message 用户本轮消息
     * @param context 已加载上下文
     * @param profile 用户学习画像
     * @return LLM 分析结果
     */
    public AppTutoringLlmAnalysisResult analyzeIfEnabled(String message,
                                                         AppTutoringLoadedContext context,
                                                         AppLearningProfile profile) {
        if (!properties.isLlmAnalysisEnabled()) {
            return AppTutoringLlmAnalysisResult.empty();
        }
        try {
            String prompt = promptBuilder.build(message, context, profile);
            CapabilityExecuteResponse response = runtimeHarness.execute(new CapabilityExecuteRequest(
                    properties.getAnalyzeCapabilityCode(),
                    Map.of("question", prompt),
                    Map.of("conversationEnabled", false, "analysisMode", true)));
            return parse(extractText(response));
        } catch (Exception ex) {
            log.warn("tutoring llm analysis failed, fallback to rules", ex);
            return AppTutoringLlmAnalysisResult.empty();
        }
    }

    /**
     * 解析 LLM 返回的 JSON 分析结果。
     *
     * @param raw 原始返回文本
     * @return 结构化分析结果
     */
    private AppTutoringLlmAnalysisResult parse(String raw) {
        if (!StringUtils.hasText(raw)) {
            return AppTutoringLlmAnalysisResult.empty();
        }
        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            return new AppTutoringLlmAnalysisResult(
                    text(node, "intent"),
                    text(node, "reasoningJudgment"),
                    text(node, "knowledgePoint"),
                    text(node, "mistakeSummary"),
                    node.path("confidence").asDouble(0.0));
        } catch (Exception ex) {
            log.warn("tutoring llm analysis parse failed, raw={}", abbreviate(raw), ex);
            return AppTutoringLlmAnalysisResult.empty();
        }
    }

    /**
     * 从混合文本中提取 JSON 对象片段。
     *
     * @param raw 原始文本
     * @return JSON 字符串
     */
    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return raw;
    }

    /**
     * 安全读取 JSON 节点文本字段。
     *
     * @param node  JSON 节点
     * @param field 字段名
     * @return 字段文本值
     */
    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? "" : value.asText("");
    }

    /**
     * 从能力执行响应中提取文本内容。
     *
     * @param response 能力执行响应
     * @return 文本内容
     */
    private String extractText(CapabilityExecuteResponse response) {
        if (response == null || response.result() == null) {
            return "";
        }
        ExecutionResult result = response.result();
        if (StringUtils.hasText(result.message())) {
            return result.message();
        }
        Object businessOutput = result.output().get("businessOutput");
        return businessOutput == null ? "" : String.valueOf(businessOutput);
    }

    /**
     * 截断日志输出用的原始文本。
     *
     * @param text 原始文本
     * @return 截断后的文本
     */
    private String abbreviate(String text) {
        return text.length() <= 120 ? text : text.substring(0, 120) + "…";
    }
}
