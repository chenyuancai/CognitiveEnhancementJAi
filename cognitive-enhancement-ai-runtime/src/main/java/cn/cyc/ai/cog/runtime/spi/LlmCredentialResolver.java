package cn.cyc.ai.cog.runtime.spi;

/**
 * LLM 凭证解析器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface LlmCredentialResolver {

    /**
     * 校验并返回模型调用所需 API Key。
     *
     * @param apiKey 提供商 API Key
     * @return 校验后的 API Key
     */
    String resolve(String apiKey);
}
