package cn.cyc.ai.cog.runtime.session.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Runtime 会话上下文配置。
 *
 * @author cyc
 */
@ConfigurationProperties(prefix = "cog.runtime.conversation")
public class ConversationProperties {

    /**
     * 是否启用会话上下文。
     */
    private boolean enabled = true;

    /**
     * 每次注入的最大历史消息数。
     */
    private int maxHistoryMessages = 10;

    /**
     * 单条历史消息最大字符数。
     */
    private int maxMessageChars = 2000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxHistoryMessages() {
        return maxHistoryMessages;
    }

    public void setMaxHistoryMessages(int maxHistoryMessages) {
        this.maxHistoryMessages = maxHistoryMessages;
    }

    public int getMaxMessageChars() {
        return maxMessageChars;
    }

    public void setMaxMessageChars(int maxMessageChars) {
        this.maxMessageChars = maxMessageChars;
    }
}
