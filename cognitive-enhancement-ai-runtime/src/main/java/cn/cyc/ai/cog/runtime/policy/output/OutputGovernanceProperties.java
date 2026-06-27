package cn.cyc.ai.cog.runtime.policy.output;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 输出治理配置。
 *
 * @author cyc
 */
@Component
@ConfigurationProperties(prefix = "cog.runtime.output-governance")
public class OutputGovernanceProperties {

    /**
     * 日志脱敏最大保留长度。
     */
    private int maxLogContentLength = 2000;

    /**
     * 额外禁止输出关键词（大小写不敏感子串匹配）。
     */
    private List<String> forbiddenKeywords = new ArrayList<>();

    public int getMaxLogContentLength() {
        return maxLogContentLength;
    }

    public void setMaxLogContentLength(int maxLogContentLength) {
        this.maxLogContentLength = maxLogContentLength;
    }

    public List<String> getForbiddenKeywords() {
        return forbiddenKeywords;
    }

    public void setForbiddenKeywords(List<String> forbiddenKeywords) {
        this.forbiddenKeywords = forbiddenKeywords == null ? new ArrayList<>() : forbiddenKeywords;
    }
}
