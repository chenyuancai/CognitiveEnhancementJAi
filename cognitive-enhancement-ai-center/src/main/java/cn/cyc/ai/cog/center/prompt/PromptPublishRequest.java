package cn.cyc.ai.cog.center.prompt;

/**
 * 发布 Prompt 版本请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptPublishRequest(String promptCode, String version) {
}
