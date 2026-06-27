package cn.cyc.ai.cog.center.prompt;

/**
 * 下线 Prompt 版本请求。
 *
 * @param version 待下线版本号
 * @author cyc
 */
public record PromptOfflineRequest(String promptCode, String version) {
}
