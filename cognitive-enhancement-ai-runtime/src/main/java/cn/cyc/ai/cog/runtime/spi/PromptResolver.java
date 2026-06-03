package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;

/**
 * Prompt 解析器。
 *
 * @author cyc
 */
public interface PromptResolver {

    /**
     * 为当前执行上下文选择 Prompt 模板。
     *
     * @param context 运行时上下文
     * @return 解析到的 Prompt 模板；未命中时返回 {@code null}
     */
    PromptTemplate resolve(ExecutionContext context);

    /**
     * 将 Prompt 模板渲染成最终提示词文本。
     *
     * @param promptTemplate Prompt 模板
     * @param context        运行时上下文
     * @return 渲染后的提示词
     */
    String render(PromptTemplate promptTemplate, ExecutionContext context);
}
