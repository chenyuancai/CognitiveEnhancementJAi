package cn.cyc.ai.cog.runtime.spi;

/**
 * LLM 凭证解析器。
 *
 * @author cyc
 */
public interface LlmCredentialResolver {

    /**
     * 解析模型调用所需凭证。
     *
     * @param credentialRef 凭证引用
     * @return 解析后的凭证
     */
    String resolve(String credentialRef);
}
