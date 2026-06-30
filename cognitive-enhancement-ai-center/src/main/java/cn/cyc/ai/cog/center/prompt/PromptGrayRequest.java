package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;

/**
 * 配置 Prompt 灰度规则请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptGrayRequest(String promptCode, PromptGrayRule grayRule) {
}
