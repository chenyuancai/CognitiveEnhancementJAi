package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;

/**
 * 配置 Prompt 灰度规则请求。
 *
 * @param grayRule 灰度规则
 * @author cyc
 */
public record PromptGrayRequest(String promptCode, PromptGrayRule grayRule) {
}
