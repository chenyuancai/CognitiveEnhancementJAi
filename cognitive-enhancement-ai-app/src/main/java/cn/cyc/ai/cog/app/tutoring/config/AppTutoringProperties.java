package cn.cyc.ai.cog.app.tutoring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * C 端 AI 学习辅导配置属性，绑定 {@code cog.app.tutoring} 前缀。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.app.tutoring")
public class AppTutoringProperties {

    /** 主对话运行时能力编码。 */
    private String runtimeCapabilityCode = "capability.chat.generate";

    /** LLM 分析能力编码。 */
    private String analyzeCapabilityCode = "capability.tutoring.analyze";

    /** Prompt 注入的最大历史消息条数。 */
    private int contextMaxMessages = 10;

    /** 单条历史消息注入 Prompt 的最大字符数。 */
    private int contextMaxCharsPerMessage = 500;

    /** Redis 热历史缓存的最大消息条数。 */
    private int hotHistoryMaxMessages = 20;

    /** 热历史缓存 TTL（天）。 */
    private int hotHistoryTtlDays = 30;

    /** 访问门禁预检所需额度。 */
    private long preflightTokenAmount = 1L;

    /** 引用内容截断最大字符数。 */
    private int referenceMaxChars = 800;

    /** 会话摘要缓存 TTL（天）。 */
    private int summaryTtlDays = 60;

    /** 学习状态缓存 TTL（天）。 */
    private int learningStateTtlDays = 60;

    /** 触发会话摘要生成的消息条数阈值。 */
    private int summaryTriggerMessageCount = 12;

    /** 生成摘要后保留的最近消息条数。 */
    private int summaryKeepRecentMessages = 6;

    /** 摘要片段最大字符数。 */
    private int summarySnippetMaxChars = 80;

    /** 卡住次数触发分步讲解的阈值。 */
    private int stuckFallbackThreshold = 2;

    /** 是否启用 LLM 意图分析。 */
    private boolean llmAnalysisEnabled = false;

    /** 是否启用 LLM 会话摘要。 */
    private boolean llmSummaryEnabled = false;

    /** 学习画像缓存 TTL（天）。 */
    private int profileTtlDays = 90;

    /** 是否启用会话标题重写。 */
    private boolean titleRewriteEnabled = true;

    /** 引导类策略回答的最大句子数。 */
    private int overHelpingMaxSentences = 6;

    /** 是否启用回答输出治理。 */
    private boolean governanceEnabled = true;
}
