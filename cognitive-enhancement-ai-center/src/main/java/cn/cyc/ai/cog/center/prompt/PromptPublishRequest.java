package cn.cyc.ai.cog.center.prompt;

/**
 * 发布 Prompt 版本请求。
 *
 * @param version 待发布版本号
 * @author cyc
 */
public record PromptPublishRequest(String promptCode, String version) {
}
