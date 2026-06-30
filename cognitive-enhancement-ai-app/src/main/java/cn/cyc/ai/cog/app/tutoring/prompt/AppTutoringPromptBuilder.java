package cn.cyc.ai.cog.app.tutoring.prompt;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringLoadedContext;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringResolvedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * C 端 AI 学习辅导 Prompt 组装器，将策略、画像与上下文注入模型输入。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringPromptBuilder {

    /**
     * 组装本轮对话的完整 Prompt 文本。
     *
     * @param message         用户本轮消息
     * @param blueprint       教学蓝图
     * @param context         已加载上下文
     * @param profile         用户学习画像
     * @param activePlanJson  活跃学习计划 JSON 片段
     * @return Prompt 文本
     */
    public String build(String message,
                        AppTutoringBlueprint blueprint,
                        AppTutoringLoadedContext context,
                        AppLearningProfile profile,
                        String activePlanJson) {
        StringBuilder builder = new StringBuilder();
        builder.append("""
                你是 C 端学习辅导型 AI 助手。请严格按教学策略输出，不要为了追问而追问。
                策略: %s
                意图: %s
                策略原因: %s
                下一步动作: %s
                要求:
                1. 一次最多提出一个问题。
                2. 适合引导时先提示再追问。
                3. 用户明确要直接答案时直接回答，但保留简短思考提示。
                4. 不要编造引用来源。
                """.formatted(
                blueprint.getSelectedStrategy(),
                blueprint.getIntent(),
                blueprint.getStrategyReason(),
                blueprint.getNextAction().getType()));
        appendProfile(builder, profile);
        if (StringUtils.hasText(activePlanJson)) {
            builder.append("\n【活跃学习计划】\n").append(activePlanJson).append('\n');
        }
        if (StringUtils.hasText(context.sessionSummary())) {
            builder.append("\n【会话摘要】\n").append(context.sessionSummary()).append('\n');
        }
        if (!CollectionUtils.isEmpty(context.promptMessages())) {
            builder.append("\n【最近对话】\n");
            for (AppTutoringCachedMessage item : context.promptMessages()) {
                builder.append(roleLabel(item.role())).append(": ").append(item.content()).append('\n');
            }
        }
        appendResolvedContext(builder, context.resolvedContext());
        builder.append("\n【用户本轮问题】\n").append(message);
        return builder.toString();
    }

    /**
     * 将学习画像信息追加到 Prompt。
     *
     * @param builder Prompt 构建器
     * @param profile 用户学习画像
     */
    private void appendProfile(StringBuilder builder, AppLearningProfile profile) {
        if (profile == null) {
            return;
        }
        builder.append("\n【学习画像】\n");
        builder.append("整体掌握度: ").append(profile.getOverallMastery()).append('\n');
        if (!CollectionUtils.isEmpty(profile.getWeakTopics())) {
            builder.append("薄弱主题: ").append(String.join("、", profile.getWeakTopics())).append('\n');
        }
    }

    /**
     * 将解析后的引用上下文追加到 Prompt。
     *
     * @param builder  Prompt 构建器
     * @param resolved 解析后的引用上下文
     */
    private void appendResolvedContext(StringBuilder builder, AppTutoringResolvedContext resolved) {
        if (resolved == null) {
            return;
        }
        if (StringUtils.hasText(resolved.getSelectedText())) {
            builder.append("\n【用户选中文本】\n").append(resolved.getSelectedText()).append('\n');
        }
        if (!CollectionUtils.isEmpty(resolved.getMessageSnippets())) {
            builder.append("\n【引用历史消息】\n");
            for (AppTutoringResolvedContext.ResolvedMessageSnippet snippet : resolved.getMessageSnippets()) {
                builder.append(snippet.getRole()).append(": ").append(snippet.getContent()).append('\n');
            }
        }
        if (!CollectionUtils.isEmpty(resolved.getKnowledgeSnippets())) {
            builder.append("\n【指定知识资料】\n");
            for (AppTutoringResolvedContext.ResolvedKnowledgeSnippet snippet : resolved.getKnowledgeSnippets()) {
                builder.append(snippet.getTitle()).append(" - ").append(snippet.getExcerpt()).append('\n');
            }
        }
        if (!CollectionUtils.isEmpty(resolved.getFileSnippets())) {
            builder.append("\n【引用文件】\n");
            for (AppTutoringResolvedContext.ResolvedFileSnippet snippet : resolved.getFileSnippets()) {
                builder.append(snippet.getFileName()).append(" - ").append(snippet.getExcerpt()).append('\n');
            }
        }
    }

    /**
     * 将消息角色编码转换为中文展示标签。
     *
     * @param role 消息角色编码
     * @return 中文角色标签
     */
    private String roleLabel(String role) {
        if ("ASSISTANT".equalsIgnoreCase(role)) {
            return "助手";
        }
        return "用户";
    }
}
