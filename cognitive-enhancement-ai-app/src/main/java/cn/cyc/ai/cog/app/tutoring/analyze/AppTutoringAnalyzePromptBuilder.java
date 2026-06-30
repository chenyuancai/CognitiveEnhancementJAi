package cn.cyc.ai.cog.app.tutoring.analyze;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringLoadedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * LLM 分析专用 Prompt 组装器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringAnalyzePromptBuilder {

    /**
     * 组装 LLM 意图与思路分析 Prompt。
     *
     * @param message 用户本轮消息
     * @param context 已加载上下文
     * @param profile 用户学习画像
     * @return 分析 Prompt 文本
     */
    public String build(String message, AppTutoringLoadedContext context, AppLearningProfile profile) {
        StringBuilder builder = new StringBuilder();
        builder.append("""
                你是学习辅导分析器。只输出 JSON，不要解释。
                字段: intent, reasoningJudgment, knowledgePoint, mistakeSummary, confidence
                intent 取值: CONCEPT_EXPLANATION, MISTAKE_ANALYSIS, PROBLEM_SOLVING, FACT_QA, SUMMARY_REVIEW, LEARNING_PLAN
                reasoningJudgment 取值: CORRECT, INCORRECT, UNKNOWN
                """);
        if (profile != null && !CollectionUtils.isEmpty(profile.getWeakTopics())) {
            builder.append("\n薄弱主题: ").append(String.join(",", profile.getWeakTopics())).append('\n');
        }
        if (!CollectionUtils.isEmpty(context.promptMessages())) {
            builder.append("\n最近对话:\n");
            for (AppTutoringCachedMessage item : tail(context.promptMessages(), 4)) {
                builder.append(item.role()).append(": ").append(item.content()).append('\n');
            }
        }
        builder.append("\n用户本轮: ").append(message);
        return builder.toString();
    }

    /**
     * 截取消息列表尾部指定条数。
     *
     * @param messages 消息列表
     * @param max      最大条数
     * @return 尾部消息列表
     */
    private List<AppTutoringCachedMessage> tail(List<AppTutoringCachedMessage> messages, int max) {
        if (messages.size() <= max) {
            return messages;
        }
        return messages.subList(messages.size() - max, messages.size());
    }
}
