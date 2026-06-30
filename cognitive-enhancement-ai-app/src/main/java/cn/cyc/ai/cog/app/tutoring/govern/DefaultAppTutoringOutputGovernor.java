package cn.cyc.ai.cog.app.tutoring.govern;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringGovernanceResult;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTeachingStrategy;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 辅导输出治理默认实现：约束过度代做与无引用时的资料幻觉。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultAppTutoringOutputGovernor implements AppTutoringOutputGovernor {

    /** 中英文句子分隔正则。 */
    private static final Pattern SENTENCE_SPLIT = Pattern.compile("(?<=[。！？!?])");

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造默认输出治理器。
     *
     * @param properties 学习辅导配置属性
     */
    public DefaultAppTutoringOutputGovernor(AppTutoringProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    /**
     * 执行govern。
     * @return 执行结果
     */
    @Override
    public AppTutoringGovernanceResult govern(String answer,
                                              AppTutoringBlueprint blueprint,
                                              AppTutoringStrategyDecision decision) {
        if (!properties.isGovernanceEnabled() || !StringUtils.hasText(answer)) {
            return AppTutoringGovernanceResult.passThrough(answer);
        }
        List<String> violations = new ArrayList<>();
        String governed = answer;
        if (shouldLimitDirectness(decision.strategy())) {
            governed = limitSentences(governed, properties.getOverHelpingMaxSentences(), violations);
        }
        if (blueprint.getContextUsed() != null
                && CollectionUtils.isEmpty(blueprint.getContextUsed().getKnowledgeRefs())
                && CollectionUtils.isEmpty(blueprint.getContextUsed().getFileRefs())
                && containsMaterialHallucination(governed)) {
            governed = governed.replaceAll("根据资料|参考资料|文档显示", "从已知信息看");
            violations.add("MATERIAL_HALLUCINATION");
        }
        AppTutoringGovernanceResult result = new AppTutoringGovernanceResult();
        result.setAnswer(governed);
        result.setViolated(!violations.isEmpty());
        result.setViolations(violations);
        return result;
    }

    /**
     * 判断当前策略是否需要限制回答的直接性。
     *
     * @param strategy 教学策略
     * @return 是否需要限制
     */
    private boolean shouldLimitDirectness(AppTeachingStrategy strategy) {
        return strategy == AppTeachingStrategy.HINT_THEN_QUESTION
                || strategy == AppTeachingStrategy.SOCRATIC_QUESTIONING;
    }

    /**
     * 限制回答句子数量，超出时追加引导语。
     *
     * @param answer       原始回答
     * @param maxSentences 最大句子数
     * @param violations   违规记录列表
     * @return 治理后的回答
     */
    private String limitSentences(String answer, int maxSentences, List<String> violations) {
        String[] parts = SENTENCE_SPLIT.split(answer.trim());
        if (parts.length <= maxSentences) {
            return answer;
        }
        violations.add("OVER_HELPING");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maxSentences && i < parts.length; i++) {
            builder.append(parts[i]);
        }
        builder.append("先想想关键条件，再告诉我你的下一步思路。");
        return builder.toString();
    }

    /**
     * 检测回答是否包含无引用支撑的资料性表述。
     *
     * @param answer 回答文本
     * @return 是否疑似资料幻觉
     */
    private boolean containsMaterialHallucination(String answer) {
        String normalized = answer.toLowerCase(Locale.ROOT);
        return normalized.contains("根据资料") || normalized.contains("文档显示");
    }
}
