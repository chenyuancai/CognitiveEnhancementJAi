package cn.cyc.ai.cog.runtime.session.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Runtime 会话上下文配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 判断是否为是否启用。
     * @return 是否满足条件
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用。
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取MaxHistoryMessages。
     * @return MaxHistoryMessages
     */
    public int getMaxHistoryMessages() {
        return maxHistoryMessages;
    }

    /**
     * 设置MaxHistoryMessages。
     *
     * @param maxHistoryMessages maxHistoryMessages
     */
    public void setMaxHistoryMessages(int maxHistoryMessages) {
        this.maxHistoryMessages = maxHistoryMessages;
    }

    /**
     * 获取Max消息Chars。
     * @return Max消息Chars
     */
    public int getMaxMessageChars() {
        return maxMessageChars;
    }

    /**
     * 设置Max消息Chars。
     *
     * @param maxMessageChars max消息Chars
     */
    public void setMaxMessageChars(int maxMessageChars) {
        this.maxMessageChars = maxMessageChars;
    }
}
