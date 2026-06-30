package cn.cyc.ai.cog.runtime.reflection;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 自反思配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.reflection")
public class ReflectionProperties {

    /**
     * 是否启用自反思能力。
     */
    private boolean enabled = true;

    /**
     * 单次执行最大反思重试次数。
     */
    private int maxRetries = 1;

    /**
     * 触发反思的最小回答长度。
     */
    private int minAnswerLength = 8;

    /**
     * 回答中包含以下片段时触发反思。
     */
    private List<String> failureKeywords = new ArrayList<>(List.of(
            "无法回答",
            "不知道",
            "I don't know",
            "cannot answer"
    ));
}
